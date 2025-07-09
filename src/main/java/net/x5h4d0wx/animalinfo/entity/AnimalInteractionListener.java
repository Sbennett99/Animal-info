package net.x5h4d0wx.animalinfo.entity;


import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnimalInteractionListener implements UseEntityCallback {
    public static final double HORSE_SPEED_CONVERSION = 42.133333333333333;
    public static final double JUMP_A = 4.125;
    public static final double JUMP_B = 1.125;
    public static AtomicBoolean exec_scheduled = new AtomicBoolean(false);
    public static AtomicBoolean exec_ended = new AtomicBoolean(true);
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    
    public AnimalInteractionListener(){
        executor.setMaximumPoolSize(2);
    }
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        Text outText = null;
        // CommandDispatcher<FabricClientCommandSource> test = ClientCommandManager.getActiveDispatcher();
        // player.sendMessage(Text.of(String.valueOf(test==null)), true);
        exec_scheduled.set(false);
        if (player.hasVehicle() || player.isSneaking()) {
            switch (entity) {
                case PandaEntity panda:
                    // From the original Panda Info 
                    outText = Texts.join(List.of(
                            Text.translatable("gui.panda-info.main_gene",
                                    Text.translatable("gui.panda-info.gene." + panda.getMainGene()))
                                    .formatted(Formatting.GOLD),
                            Text.translatable("gui.panda-info.hidden_gene",
                                    Text.translatable("gui.panda-info.gene." + panda.getHiddenGene()))
                                    .formatted(Formatting.AQUA),
                            Text.translatable("gui.panda-info.product_gene",
                                    Text.translatable("gui.panda-info.gene." + panda.getProductGene()))
                                    .formatted(Formatting.GREEN)),
                            Text.of(" | ")); 
                    break;
                case AbstractHorseEntity horsish:
                    final double blockSpeed = horsish.getAttributeValue(EntityAttributes.MOVEMENT_SPEED)* HORSE_SPEED_CONVERSION;
                    final double blockJump = jumpStrToBlocks(horsish.getAttributeValue(EntityAttributes.JUMP_STRENGTH));
                    final double health = horsish.getAttributeValue(EntityAttributes.MAX_HEALTH);
                    outText = Text.of(String.format("Speed: %1$.2f | Jump %2$.2f | Hearts %3$.2f ",blockSpeed, blockJump, health/2));

                    break;
                default:
                    break;
            }
            if (outText != null) {
                sendLingeringMessage(player, entity, outText);
            }
        }
        return ActionResult.PASS;
    }

    private static double jumpStrToBlocks(double jumpPower){
        return (JUMP_A * jumpPower * jumpPower) + (JUMP_B * jumpPower);
    }

    private static void lingeringMessageHandeler(PlayerEntity player, Entity entity ,Text message){
        player.sendMessage(message, true);
        if(player.canInteractWithEntity(entity, 0) && exec_scheduled.get()){
            executor.schedule(() -> lingeringMessageHandeler(player, entity, message), 100, TimeUnit.MILLISECONDS);
        }else{
            exec_ended.set(true);
        }
    }

    private static void sendLingeringMessage(PlayerEntity player, Entity entity ,Text message){
        final String messageCopy = message.getString();
        while (!exec_ended.get()) {}
        exec_scheduled.set(true);
        exec_ended.set(false);
        lingeringMessageHandeler(player, entity, Text.of(messageCopy));
    }
}