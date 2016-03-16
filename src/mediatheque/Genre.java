package mediatheque;
import java.io.Serializable;
/**
 * La classe <code>Genre</code> gere les types des documents
 * dans la mediatheque. Les genres sont utilises pour classifier
 * les documents.
 */
public final class Genre implements Serializable {
	/** serial version. */
	private static final long serialVersionUID = 3L;

	/** Nom du genre. */
	private String nom;

	/**
	 * Nombre de fois ou un document de ce genre a ete emprunte.
	 */
	private int nbEmprunts;

	/**
	 * Constructeur de Genre.
	 *   @param n chaine de caracteres devrivant le genre
	 */
	public Genre(final String n) {
		nom = n;
		nbEmprunts = 0;
	}

	/**
	 * Emprunter augmente le nombre de fois qu un document de
	 * ce genre a ete emprunte.
	 */
	public void emprunter() {
		nbEmprunts++;
	}

	/**
	 * getNom permet de connaitre le nom du genre.
	 *   @return nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 *  toString permet de connaitre le nom et le nombre d emprunts.
	 *   @return nom et nombre d emprunts
	 */
	@Override
	public String toString() { 
		return "Genre: " + nom + ", nbemprunts:" + nbEmprunts;
	}

	/**
	 * modifier change le nom du genre il devrait être accessible 
	 * a la mediatheque seulement.
	 * @param nouveau nouveau nom
	 */
	public void modifier(final String nouveau) {
		nom = nouveau;
	}
/**
 * retourne le nombre d'emprunts pour ce genre.
 * @return nbEmprunts
 */
	public int getNbEmprunts() {
		return nbEmprunts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (nom == null) {
			result = prime;
		} else {
			result = prime + nom.hashCode();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Genre)) {
			return false;
		}
		Genre other = (Genre) obj;
		if (nom == null) {
			if (other.nom != null) {
				return false;
			}
		} else if (!nom.equals(other.nom)) {
			return false;
		}
		return true;
	}

	/**
	 * afficherStatistiques affiche les statistiques d'emprunt
	 * du genre.
	 */
	public void afficherStatistiques() {
		System.out.println("(stat) Genre :" + this);
	}
}
