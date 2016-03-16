package mediatheque.document;

import mediatheque.Genre;
import mediatheque.Localisation;
import mediatheque.OperationImpossible;
import util.Datutil;
import util.InvariantBroken;

/**
 * La classe <code>Audio</code> gere les documents de type CD audio.
 * Ils possedent une classification, une duree de pret de
 * 4 semaines et un tarif de pret de 1 euro.
 */
public final class Audio extends Document {
	  /** serial number.	 */
	private static final long serialVersionUID = 4L;

	/**
	 * Classification du CD, format libre.
	 */
	private String classification;

	/**
	 * Duree de pret en nombre de jours.
	 */
	public static final int DUREE = 4 * Datutil.DAYSINWEEK;

	/**
	 * Tarif du pret.
	 */
	public static final  double TARIF = 1.0;

	/**
	 * Nombre d'emprunts total de CD.
	 */
	private static int nbEmpruntsTotal = 0;

	// Les methodes
	/**
	 * Constructeur de CD audio avec les attributs valorises. Par
	 * defaut, une CD audio n'est pas empruntable.
	 *   @param code Code du document
	 *   @param localisation Localisation du document
	 *   @param titre Titre du document
	 *   @param auteur Auteur du document
	 *   @param annee Annee de sortie du document
	 *   @param genre Genre du document
	 *   @param classif Classification du document
	 *   @throws OperationImpossible en relai du constructeur de Document
	 *   @throws InvariantBroken en relai du constructeur de Document
	 */
	public Audio(final String code, final Localisation localisation, 
			final String titre,
			final String auteur, final String annee, final Genre genre,
			final String classif
			) throws OperationImpossible, InvariantBroken {
		super(code, localisation, titre, auteur, annee, genre);
		if (classif == null) {
			throw new OperationImpossible("Ctr Audio classification = "
					+ classif);
		}
		this.classification = classif;
	}

	/**
	 * retourne le nombre d'emprunts (statistique)
	 * de la classe.
	 *   @return Nombre d'emprunts total
	 */
	public static int getNbEmpruntsTotal() {
		return nbEmpruntsTotal;
	}

	/**
	 * Retourne la classification.
	 *	@return Classification du CD
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * <TT>emprunter()</TT> est appelee lors de l'emprunt d'un document.
	 * Les statistiques sont mises a jour.
	 * @return true ou lève une exception
	 * @exception InvariantBroken relai de l'exception de la classe parente
	 * @exception OperationImpossible relai de l'exception de la classe parente
	 */
	@Override
	public boolean emprunter() 
			throws InvariantBroken, OperationImpossible {
		super.emprunter();
		nbEmpruntsTotal++;
		return true;
	}

	// Methodes de l'interface Empruntable

	/**
	 * <TT>dureeEmprunt</TT> retourne la duree nominale de pret
	 * du document en nombre de jours.
	 *    @return Duree de pret
	 */
	@Override
	public int dureeEmprunt() {
		return DUREE;
	}

	/**
	 * <TT>tarifEmprunt</TT> retourne le tarif nominal du pret
	 * du document.
	 *    @return Tarif du pret
	 */
	@Override
	public double tarifEmprunt() {
		return TARIF;
	}
	@Override
	public String toString() {
		String s = "[Audio] " + super.toString();
		s += " " + classification;
		return s;
	}
}
