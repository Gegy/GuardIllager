package illager.guardillagers.entity;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import illager.guardillagers.GuardIllagers;
import illager.guardillagers.init.IllagerItems;
import illager.guardillagers.init.IllagerSoundsRegister;
import illager.guardillagers.utils.IllagerShieldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityGuardIllager extends AbstractIllager {


    private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, 0)).setSaved(false);
    private static final DataParameter<Boolean> IS_DRINKING = EntityDataManager.createKey(EntityGuardIllager.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_STRONG = EntityDataManager.createKey(EntityGuardIllager.class, DataSerializers.BOOLEAN);

    private int potionUseTimer;

    public double prevCapeX, prevCapeY, prevCapeZ;
    public double capeX, capeY, capeZ;

    public EntityGuardIllager(World world) {
        super(world);
        this.setSize(0.6F, 1.95F);
        this.setDropChance(EntityEquipmentSlot.OFFHAND, 0.4F);
        ((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, AbstractIllager.class));
        this.targetTasks.addTask(2, new EntityGuardIllager.AIAttackPlayer());
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.348F);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(22.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(7.0D);
    }

    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(IS_DRINKING, false);
        this.getDataManager().register(IS_STRONG, false);
    }


    public void setDrinkingPotion(boolean drinkingPotion) {
        this.getDataManager().set(IS_DRINKING, drinkingPotion);
    }

    public boolean isDrinkingPotion() {
        return this.getDataManager().get(IS_DRINKING);
    }

    public boolean isStrong() {
        return this.dataManager.get(IS_STRONG);
    }

    public void setStrong(boolean strong) {
        this.dataManager.set(IS_STRONG, strong);
        if (strong) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D);
        }

    }


    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Strong", this.isStrong());
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setStrong(compound.getBoolean("Strong"));
    }

    public void onLivingUpdate() {
        if (!this.world.isRemote) {
            if (this.isDrinkingPotion()) {
                if (this.potionUseTimer-- <= 0) {
                    this.setDrinkingPotion(false);
                    ItemStack itemstack = this.getHeldItemOffhand();
                    if (isStrong()) {
                        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, IllagerShieldUtils.getIllagerShield());
                    } else {
                        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    if (itemstack.getItem() == Items.POTIONITEM) {
                        List<PotionEffect> list = PotionUtils.getEffectsFromStack(itemstack);
                        if (list != null) {
                            for (PotionEffect potioneffect : list) {
                                this.addPotionEffect(new PotionEffect(potioneffect));
                            }
                        }
                    }

                    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER);
                }
            } else {
                PotionType potiontype = null;

                if (this.rand.nextFloat() < 0.004F && this.getHealth() < this.getMaxHealth()) {
                    potiontype = PotionTypes.HEALING;
                } else if (this.rand.nextFloat() < 0.008F && this.getAttackTarget() != null && !this.isPotionActive(MobEffects.SPEED) && this.getAttackTarget().getDistanceSq(this) > 121.0D) {
                    potiontype = PotionTypes.SWIFTNESS;
                }

                if (potiontype != null) {
                    this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype));
                    this.potionUseTimer = this.getHeldItemOffhand().getMaxItemUseDuration();
                    this.setDrinkingPotion(true);
                    this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
                    IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    iattributeinstance.removeModifier(MODIFIER);
                    iattributeinstance.applyModifier(MODIFIER);
                }
            }
        }

        super.onLivingUpdate();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.updateCape();
    }


    private void updateCape() {
        double elasticity = 0.25;
        double gravity = -0.1;

        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;

        this.capeY += gravity;

        this.capeX += (this.posX - this.capeX) * elasticity;
        this.capeY += (this.posY - this.capeY) * elasticity;
        this.capeZ += (this.posZ - this.capeZ) * elasticity;
    }

    public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
        super.setRevengeTarget(livingBase);
        if (livingBase != null) {
            if (livingBase instanceof EntityPlayer) {

                if (!((EntityPlayer) livingBase).isCreative() && this.isEntityAlive()) {
                    this.world.setEntityState(this, (byte) 13);
                }
            }
        }

    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return GuardIllagers.LOOT_TABLE;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAggressive() {
        return this.isAggressive(1);
    }

    public void setAggressive(boolean p_190636_1_) {
        this.setAggressive(1, p_190636_1_);
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose() {
        return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.CROSSED;
    }


    public void setHomePos() {
        this.setHomePosAndDistance(new BlockPos(this), 26);
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData) {
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        if (this.rand.nextInt(5) == 0) {
            this.setStrong(true);
        }
        return super.onInitialSpawn(difficulty, entityLivingData);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        if (isStrong()) {
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, IllagerShieldUtils.getIllagerShield());
        }
    }


    protected void updateAITasks() {
        super.updateAITasks();
        this.setAggressive(this.getAttackTarget() != null);
    }

    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.getEntityBoundingBox().minY);
        int k = MathHelper.floor(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        return this.rand.nextInt(5) == 0 && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.posY > 60.0D && this.posY < 90.0D && !this.world.canSeeSky(new BlockPos(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ)) && this.world.getLight(blockpos) > 6 && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    public boolean isOnSameTeam(Entity entityIn) {
        if (super.isOnSameTeam(entityIn)) {
            return true;
        } else if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getCreatureAttribute() == EnumCreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAggressive()) {
            return IllagerSoundsRegister.GUARDILLAGER_ANGRY;
        } else {
            return IllagerSoundsRegister.GUARDILLAGER_AMBIENT;
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return IllagerSoundsRegister.GUARDILLAGER_DIE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IllagerSoundsRegister.GUARDILLAGER_HURT;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        SoundType soundtype = blockIn.getSoundType(world.getBlockState(pos), world, pos, this);

        SoundEvent soundEvent = IllagerSoundsRegister.GUARDILLAGER_STEP;

        if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
            soundtype = Blocks.SNOW_LAYER.getSoundType();
            this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        } else if (!blockIn.getDefaultState().getMaterial().isLiquid()) {
            this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
            this.playSound(soundEvent, 0.2F, soundtype.getPitch());
        }
    }

    public class AIAttackPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
        private EntityPlayer player;

        public AIAttackPlayer() {
            super(EntityGuardIllager.this, EntityPlayer.class, true);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (super.shouldExecute()) {
                this.targetEntity = this.taskOwner.world.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double) this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>() {
                    @Nullable
                    public Double apply(@Nullable EntityPlayer p_apply_1_) {
                        ItemStack itemstack = p_apply_1_.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                        if (itemstack.getItem() == IllagerItems.GUARD_HELMET) {
                            return 0.5D;
                        }

                        return 1.0D;
                    }
                }, (Predicate<EntityPlayer>) this.targetEntitySelector);
                return this.targetEntity != null;
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting();
        }
    }
}
