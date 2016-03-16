package mediatheque.client;

import java.io.Serializable;
/**
 * La classe CategorieClient gere les categories des clients.
 */
public final class CategorieClient implements Serializable {
	/** serial number.	 */
	private static final long serialVersionUID = 2L;
	/** Nom de la categorie.  */
	private String nomCat;

	/** Nombre d'emprunts maximal tarif normal.	 */
	private int nbEmpruntMax;

	/** Cotisation annuelle.	 */
	private double cotisation;
	/** Coefficient applique a la duree du document pour les abonnes. */
	private double coefDuree;
	/** Coefficient appliquable au tarif du document. */
	private double coefTarif;
	/**
	 * is the reduction code used the client associated to that category.
	 */
	private boolean codeReducActif;

	/**
	 * Constructeur complet.
	 * @param nom nom de la categorie
	 * @param max nbre d'emprunt max
	 * @param cot cotisation
	 * @param coefDur coefficient de duree
	 * @param coefTar coefficient sur tarif
	 * @param codeReducAct is the reduction code in client used
	 */

	public CategorieClient(final String nom, final int max, final double cot, 
			final double coefDur, final double coefTar, 
			final boolean codeReducAct) {
		nomCat = nom;
		nbEmpruntMax = max;
		cotisation = cot;
		this.coefDuree = coefDur;
		this.coefTarif = coefTar;
		this.codeReducActif = codeReducAct;
	}
	/**
	 * Constructeur partiel pour recherche.
	 * @param nom nom de la categorie.
	 */
	public CategorieClient(final String nom) {
		nomCat = nom;
		nbEmpruntMax = 0;
		cotisation = 0;
		coefDuree = 0;
		coefTarif = 0;
		codeReducActif = false;
	}
	/**
	 * Modification du nom de categorie.
	 * @param nouveau nom
	 */
	public void modifierNom(final String nouveau) {
		nomCat = nouveau;
	}
	/**
	 * Modification du nombre maximum d'emprunts de la categorie.
	 * @param max nouveau max.
	 */
	public void modifierMax(final int max) {
		nbEmpruntMax = max;
	}
	/**
	 * Modification de la cotisation.
	 * @param cot nouvelle valeur.
	 */
	public void modifierCotisation(final double cot) {
		cotisation = cot;
	}
	/**
	 * Modification du coefficient de durée.
	 * @param coefDur nouveau coefficient.
	 */
	public void modifierCoefDuree(final double coefDur) {

		this.coefDuree = coefDur;
	}
	/**
	 * Modification du coefficient de tarif.
	 * @param coefTar nouveau coefficient.
	 */
	public void modifierCoefTarif(final double coefTar) {
		this.coefTarif = coefTar;
	}
	/**
	 * Modification du booleen rendant le code de reduction actif
	 * dans les clients de cette catégorie.
	 * @param codeReducAct nouvelle valeur.
	 */
	public void modifierCodeReducActif(final boolean codeReducAct) {
		this.codeReducActif = codeReducAct;
	}

	/**
	 * retourne le nombre d'emprunts maximum pour cette categorie.
	 * @return the nbEmpruntMax
	 */
	public int getNbEmpruntMax() {
		return nbEmpruntMax;
	}

	/**
	 * retourne la cotisation pour cette categorie.
	 * @return the cotisation
	 */
	public double getCotisation() {
		return cotisation;
	}

	/**
	 * retourne le coefficient de durée pour cette categorie.
	 * @return the coefDuree
	 */
	public double getCoefDuree() {
		return coefDuree;
	}

	/**
	 * retourne le coefficient de tarif pour cette categorie.
	 * @return the coefTarif
	 */
	public double getCoefTarif() {
		return coefTarif;
	}

	@Override
	public String toString() {
		return "Categorie : " + nomCat;
	}
	/**
	 * retourne le nom de cette categorie.
	 * @return nom de cette categorie
	 */
	public String getNom() {
		return nomCat;
	}
	/**
	 * Retourne true si le code de reduction est actif
	 * dans les clients de cette catégorie.
	 * @return true si vrai.
	 */
	public boolean getCodeReducUtilise() {
		return codeReducActif;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime;
		if (nomCat != null) {
			result += nomCat.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CategorieClient other = (CategorieClient) obj;
		if (nomCat == null) {
			if (other.nomCat != null) {
				return false;
			}
		} else if (!nomCat.equals(other.nomCat)) {
			return false;
		}
		return true;
	}
}
