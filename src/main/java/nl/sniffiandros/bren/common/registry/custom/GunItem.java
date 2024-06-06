package nl.sniffiandros.bren.common.registry.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.AttributeReg;
import nl.sniffiandros.bren.common.registry.ParticleReg;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GunItem extends ToolItem implements Vanishable {
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    private final SoundEvent shootSound;
    private final SoundEvent silentShootSound;


    public GunItem(Settings settings, ToolMaterial material, GunProperties gunProperties) {
        super(material, settings.maxDamageIfAbsent((int) (material.getDurability() * 1.5)));
        this.shootSound = gunProperties.sound;
        this.silentShootSound = gunProperties.silentSound;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(AttributeReg.RANGED_DAMAGE, new EntityAttributeModifier(AttributeReg.RANGED_DAMAGE_MODIFIER_ID, "Weapon modifier", gunProperties.rangedDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(AttributeReg.FIRE_RATE, new EntityAttributeModifier(AttributeReg.FIRE_RATE_MODIFIER_ID, "Weapon modifier", gunProperties.fireRate, EntityAttributeModifier.Operation.ADDITION));
        builder.put(AttributeReg.RECOIL, new EntityAttributeModifier(AttributeReg.RECOIL_MODIFIER_ID, "Weapon modifier", gunProperties.recoil, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }


    public int getMaxCapacity(ItemStack stack) {
        return 0;
    }

    public SoundEvent getShootSound() {
        return this.shootSound;
    }

    public SoundEvent getSilentShootSound() {
        return this.silentShootSound;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(getContents(stack) + " ").append(Text.translatable(String.format("desc.%s.item.machine_gun.content", Bren.MODID)))
					.formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public void onReload(PlayerEntity player) {

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            return TypedActionResult.pass(stack);
        }

        if (user instanceof IGunUser) {
            user.setCurrentHand(hand);
        }

        return super.use(world, user, hand);
    }

    public boolean isEmpty(ItemStack stack) {
        return getContents(stack) <= 0;
    }

    public int getContents(ItemStack stack) {
        return 0;
    }


    public void useBullet(ItemStack stack) {

    }

    public static void shotParticles(World world, Vec3d origin, Vec3d direction, Random random) {

        for (int i = 0; i != 8; ++i) {
            double t = Math.pow(random.nextFloat(), 1.5);

            Vec3d p = origin.add(direction.multiply(0.8 + t));
            Vec3d v = direction.multiply(0.2 * (1 - t));
            world.addParticle(ParticleReg.MUZZLE_SMOKE_PARTICLE, p.x, p.y, p.z, v.x, v.y, v.z);

        }
    }

    public static void ejectCasingParticle(World world, Vec3d origin, Vec3d direction, Random random) {
        Vec3d rotated = direction.rotateY((float) (-Math.PI/2));

        Vec3d p = origin.add(direction.multiply(.3f)).add(rotated.multiply(.26));
        Vec3d v = rotated.multiply(.15f).add(0,.5f + world.getRandom().nextFloat() * .1f,0);
        world.addParticle(ParticleReg.CASING_PARTICLE, p.x, p.y, p.z, v.x, v.y, v.z);
    }


    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    public int bulletLifespan() {
        return 35;
    }

    public float spread() {
        return 0.0f;
    }

    public int bulletAmount() {
        return 1;
    }
}
