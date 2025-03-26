package cellcorp.gameofcells.objects.Size;

public class SmallSizeUpgrade extends SizeUpgrade {
    public SmallSizeUpgrade() {
        super(1, 50, 0, "Small");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Basic expansion\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Slight glow effect";
    }
}
