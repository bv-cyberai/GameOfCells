package cellcorp.gameofcells.objects.Size;

public class LargeSizeUpgrade extends SizeUpgrade {
    public LargeSizeUpgrade() {
        super(1, 85, 2, "Large");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Significant mass\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Swirling cytoplasm";
    }
}
