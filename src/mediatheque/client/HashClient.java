package mediatheque.client;
/**
 * La classe <code>HashClient</code> permet de gérer les clés pour
 * retrouver rapidement les clients dans une Hashtable.
 * Ces attributs sont le nom, et le prenom du client.
 */
public final class HashClient implements java.io.Serializable {
	/** serial number.	 */
	private static final long serialVersionUID = 2L;

	/**
	 * Nom du client, format libre.
	 */
	private String nom;

	/**
	 * Prenom du client, format libre.
	 */
	private String prenom;
	/**
	 * Constructeur.
	 * @param n nom du client
	 * @param p prenom du client
	 */
	public HashClient(final String n, final String p) {
		this.nom = n;
		this.prenom = p;
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof HashClient)) {
			return false;
		}
		HashClient hc = (HashClient) obj;
		return (nom.equals(hc.nom) && prenom.equals(hc.prenom));
	}
	@Override
	public int hashCode() {
		// Very simple approach:
		// Using Joshua Bloch's recipe:
		final int prime = 37, magic = 17;
		int result = magic;
		if (nom != null) {
			result += nom.hashCode();
		}
		if (prenom != null) {
			result = prime * result + prenom.hashCode();
		}
		return result;
	}
	/**
	 * retourne le nom.
	 * @return nom
	 */
	public String getNom() {
		return nom;
	}
	/**
	 * retourne le prénom.
	 * @return prenom
	 */
	public String getPrenom() {
		return prenom;
	}

}
