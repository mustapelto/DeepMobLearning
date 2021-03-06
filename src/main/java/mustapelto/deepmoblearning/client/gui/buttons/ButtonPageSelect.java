package mustapelto.deepmoblearning.client.gui.buttons;

import com.google.common.collect.ImmutableList;
import mustapelto.deepmoblearning.DMLConstants;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

public class ButtonPageSelect extends ButtonBase {
    private final Direction direction;

    public ButtonPageSelect(int buttonId, int x, int y, Direction direction) {
        super(buttonId, x, y, 31, 12, DMLConstants.Gui.ButtonTextures.PAGE_SELECT);
        this.direction = direction;
    }

    @Nonnull
    @Override
    public ImmutableList<String> getTooltip() {
        switch (direction) {
            case NEXT:
                return ImmutableList.of(I18n.format("deepmoblearning.button_page_select.next"));
            case PREV:
                return ImmutableList.of(I18n.format("deepmoblearning.button_page_select.prev"));
            default:
                return ImmutableList.of();
        }
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    protected int getState() {
        return direction == Direction.NEXT ? 1 : 0;
    }

    public enum Direction {
        NEXT, PREV
    }
}
