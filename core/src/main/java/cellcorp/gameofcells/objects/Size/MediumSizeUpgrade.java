package cellcorp.gameofcells.objects.Size;

public class MediumSizeUpgrade extends SizeUpgrade {
    public MediumSizeUpgrade() {
        super(1, 65, 1, "Medium");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Visible growth\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Pulsing membrane";
    }
}
