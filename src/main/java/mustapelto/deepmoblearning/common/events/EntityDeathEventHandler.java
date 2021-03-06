package mustapelto.deepmoblearning.common.events;

import mustapelto.deepmoblearning.common.items.ItemDataModel;
import mustapelto.deepmoblearning.common.items.ItemDeepLearner;
import mustapelto.deepmoblearning.common.items.ItemGlitchArmor;
import mustapelto.deepmoblearning.common.items.ItemGlitchSword;
import mustapelto.deepmoblearning.common.metadata.MetadataDataModel;
import mustapelto.deepmoblearning.common.util.DataModelHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;
import java.util.stream.Collectors;

@EventBusSubscriber
public class EntityDeathEventHandler {
    //TODO: Add Trial Stuff
    private static final Integer entityUUIDBlacklistCap = 1000;
    private static final NonNullList<UUID> killedEntityUUIDBlacklist = NonNullList.create();

    @SubscribeEvent
    public static void dropEvent(LivingDropsEvent event) {

    }

    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer)
            handlePlayerDeath(event);
        else
            handleMobDeath(event);
    }

    private static void handlePlayerDeath(LivingDeathEvent event) {
        // TODO: implement
    }

    private static void handleMobDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();

        // If blacklist is at cap -> clear list and re-add last killed entity
        if (killedEntityUUIDBlacklist.size() >= entityUUIDBlacklistCap)
            cullEntityUUIDBlacklist();

        if (isEntityUUIDBlacklisted(entity))
            return;

        // TODO: Trial stuff

        if (event.getSource().getTrueSource() instanceof EntityPlayer)
            handlePlayerKill(event);

        killedEntityUUIDBlacklist.add(entity.getUniqueID());
    }

    private static void handlePlayerKill(LivingDeathEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();

        if (player == null)
            return;

        NonNullList<ItemStack> inventory = NonNullList.create();
        inventory.addAll(player.inventory.mainInventory);
        inventory.addAll(player.inventory.offHandInventory);

        // Find deep learners and trial keys from inventory
        NonNullList<ItemStack> deepLearners = NonNullList.create();
        deepLearners.addAll(inventory.stream()
                .filter(stack -> stack.getItem() instanceof ItemDeepLearner)
                .collect(Collectors.toList()));

        NonNullList<ItemStack> trialKeys = NonNullList.create();
        /* TODO: add
        trialKeys.addAll() */

        NonNullList<ItemStack> updatedModels = NonNullList.create();
        deepLearners.forEach(stack -> updatedModels.addAll(updateModels(stack, event, player)));

        if (updatedModels.isEmpty())
            return; // No models found -> nothing more to do

        // Chance to drop pristine matter from the model that gained data
        if (ItemGlitchArmor.isSetEquipped(player)) {
            // TODO: Don't run if player in trial
            ItemGlitchArmor.dropPristineMatter(event.getEntityLiving().world, event.getEntityLiving().getPosition(), updatedModels.get(0));
        }

        if (player.getHeldItemMainhand().getItem() instanceof ItemGlitchSword) {
            // TODO: Don't run if player in trial
            ItemStack sword = player.getHeldItemMainhand();
            if (ItemGlitchSword.canIncreaseDamage(sword)) {
                ItemGlitchSword.increaseDamage(sword, player);
            }
        }

        // TODO: Attune trial keys
    }

    // Helper Functions

    private static NonNullList<ItemStack> updateModels(ItemStack deepLearner, LivingDeathEvent event, EntityPlayerMP player) {
        NonNullList<ItemStack> deepLearnerItems = ItemDeepLearner.getContainedItems(deepLearner);
        NonNullList<ItemStack> updatedModels = NonNullList.create();

        deepLearnerItems.forEach(stack -> {
            if (!(stack.getItem() instanceof ItemDataModel))
                return;

            MetadataDataModel dataModelMetadata = DataModelHelper.getDataModelMetadata(stack);

            if (dataModelMetadata.isInvalid())
                return;

            if (dataModelMetadata.isAssociatedMob(event.getEntityLiving())) {
                DataModelHelper.addKill(stack, player);
                updatedModels.add(stack);
            }
        });
        ItemDeepLearner.setContainedItems(deepLearner, deepLearnerItems);

        return updatedModels;
    }

    // UUID Blacklist Functions

    private static void cullEntityUUIDBlacklist() {
        UUID lastUUID = killedEntityUUIDBlacklist.get(killedEntityUUIDBlacklist.size() - 1);
        killedEntityUUIDBlacklist.clear();
        killedEntityUUIDBlacklist.add(lastUUID);
    }

    private static boolean isEntityUUIDBlacklisted(EntityLivingBase entityLiving) {
        return killedEntityUUIDBlacklist.stream()
                .anyMatch(uuid -> uuid.compareTo(entityLiving.getUniqueID()) == 0);
    }
}
