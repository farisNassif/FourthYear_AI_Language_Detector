package ie.gmit.sw.language;

/**
 * - This class was taken from my Adv OOP Project -
 * 
 * Defines the structure of a k-mer and allows for attributes to be mapped and
 * compared with other k-mers for Language calculation
 */
public class KmerBody implements Comparable<KmerBody> {
	private int kmer;
	private int frequency;
	private int rank;

	/**
	 * Creates a new KmerBody object
	 * 
	 * @param kmer      The k-mer of the given text. E.G - Dodo : [DO][OD][DO]
	 * @param frequency The frequency of the given k-mers. E.G - DO in DODO has a
	 *                  frequency of 2 while OD has a frequency of 1 <i>(Assuming
	 *                  it's tiled)</i>
	 */
	public KmerBody(int kmer, int frequency) {
		super();
		this.kmer = kmer;
		this.frequency = frequency;
	}

	/**
	 * Returns a k-mer <br>
	 * <i>A k-mer that has had .hashcode() called on it </i>
	 * 
	 * @return kmer of n length
	 */
	public int getKmer() {
		return kmer;
	}

	/**
	 * Sets a k-mer <br>
	 * <i>A k-mer that has had .hashcode() called on it </i>
	 * 
	 * @param kmer of n legth
	 */
	public void setKmer(int kmer) {
		this.kmer = kmer;
	}

	/**
	 * Returns the frequency of a k-mer
	 * 
	 * @return frequency of which a k-mer appears
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency of a k-mer
	 * 
	 * @param frequency of a k-mer
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Returns the rank of a k-mer which varies on its frequency
	 * 
	 * @return rank of a k-mer
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Sets the rank of a k-mer which varies on its frequency
	 * 
	 * @param rank of a k-mer
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(KmerBody next) {
		return -Integer.compare(frequency, next.getFrequency());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + kmer + "/" + frequency + "/" + rank + "]";
	}
}