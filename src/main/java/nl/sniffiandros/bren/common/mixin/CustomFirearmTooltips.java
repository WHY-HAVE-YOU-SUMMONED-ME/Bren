package nl.sniffiandros.bren.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.registry.AttributeReg;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.UUID;

@Mixin(ItemStack.class)
public abstract class CustomFirearmTooltips {
    @Shadow
    private Item item;

    @ModifyVariable(method = "getTooltip", at = @At("STORE"), ordinal = 0)
    private boolean overrideFormatting(boolean original) {
        return original || item instanceof GunItem;
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;", ordinal = 0))
    private String modifyTooltipContents(DecimalFormat formatter, double value, @Local EntityAttributeModifier attribute) {
        String insertion = "";
        UUID attributeID = attribute.getId();
        if (attributeID == AttributeReg.FIRE_RATE_MODIFIER_ID) {
            insertion = "t";
        } else if (attributeID == AttributeReg.RECOIL_MODIFIER_ID) {
            insertion = "˚";
            value /= ((EnchantmentHelper.getLevel(EnchantmentReg.STEADY_HANDS, (ItemStack)(Object)this) * 2.6d * 0.1d) + 1);
        }
        return formatter.format(value) + insertion;
    }
}
