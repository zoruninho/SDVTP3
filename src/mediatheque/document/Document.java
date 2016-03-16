package mediatheque.document;
import mediatheque.Genre;
import mediatheque.Localisation;
import mediatheque.OperationImpossible;

import java.io.Serializable;
import util.HasInvariant;
import util.InvariantBroken;

/**
 * La classe <code>Document</code> gere les documents de la mediatheque.
 * Elles est abstraite et propose l'interface <code>Empruntable</code>.
 */
public abstract class Document 
implements Empruntable, Serializable, HasInvariant {
	/** serial number.	 */
	private static final long serialVersionUID = 3L;

	/**
	 * Code du document, unique dans la mediatheque, format libre.
	 */
	private String code;

	/**
	 * Titre du document, format libre.
	 */
	private String titre;

	/**
	 * Auteur du document, format libre.
	 */
	private String auteur;

	/**
	 * Annee de sortie du document, format libre.
	 */
	private String annee;

	/**
	 * Genre du document.
	 */
	private Genre genre;

	/**
	 * Indique si un document est empruntable. Il s'agit d'un
	 * attribut et non d'un type afin de pouvoir le modifier.
	 */
	private boolean empruntable;

	/**
	 * Indique si le document a ete emprunte.
	 */
	private boolean emprunte;

	// Informations statistiques

	/**
	 * Nombre d'emprunts du document.
	 */
	private int nbEmprunts;

	/**
	 * Localisation du document.
	 */
	private Localisation localisation;

 // 2- le(s) constructeur(s)

	/**
	 * Constructeur de document avec les attributs valorises. Par
	 * defaut, le document n'est pas empruntable.
	 *   @param co Code du document
	 *   @param loc Localisation du document
	 *   @param tit Titre du document
	 *   @param aut Auteur du document
	 *   @param an Annee de sortie du document
	 *   @param g Genre du document
	 *   @throws OperationImpossible si certains arguments sont mal initialises
	 */
	public Document(final String co, final Localisation loc, 
			final String tit, final String aut, final String an, 
			final Genre g) 
					throws OperationImpossible {
		if (co == null || loc == null || tit == null 
				|| aut == null || an == null || g == null) {
			throw new OperationImpossible("Ctr Document arguments = " 
					+ "code : " + co + ", localisation : " + loc 
					+ ", titre : " + tit + ", auteur : " + aut 
					+ ", annee : " + an + ", genre : " + g);
		}
		this.code = co;
		this.localisation = loc;
		this.titre = tit;
		this.auteur = aut;
		this.annee = an;
		this.genre = g;
		this.empruntable = false;
		this.emprunte = false;
		nbEmprunts = 0;
	}

	/**
	 * <TT>getCode</TT> retourne le code du document.
	 * @return Code du document
	 */
	public final String getCode() { return code; }

	/**
	 * <TT>getTitre</TT> retourne le titre du document.
	 *   @return Titre du document
	 */
	public final String getTitre() { return titre; }

	/**
	 * <TT>getAuteur</TT> retourne l'auteur du document.
	 *   @return Auteur du document
	 */
	public final String getAuteur() { return auteur; }

	/**
	 * <TT>getLocalisation</TT> retourne la localisation du document.
	 *   @return Localisation du document
	 */
	public final Localisation getLocalisation() { return localisation; }

	/**
	 * <TT>getAnnee</TT> retourne l'annee du document.
	 *   @return Annee du document
	 */
	public final String getAnnee() {  return annee; }

	/**
	 * <TT>getGenre</TT> retourne le genre du document.
	 *   @return Genre du document
	 */
	public final Genre getGenre() { return genre; }

	/**
	 *  retourne le nombre d'emprunts du document.
	 *   @return nb emprunts du document
	 */
	public final int getNbEmprunts() { return nbEmprunts; }


	/**
	 * <TT>equals</TT> est une surcharge de <TT>Object.equals</TT>
	 * permettant de tester l'egalite de deux documents. Il y a egalite
	 * quand les documents ont le meme code.
	 *   @param obj Operande droit
	 *   @return true si les documents ont le meme code
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Document)) {
			return false;
		}
		Document d = (Document) obj;
		return code.equals(d.code);
	}

	/**
	 * Rewrite hashCode.
	 * @return int to facilitate hash
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = prime;
		if (code != null) {
			result +=  code.hashCode();
		}
		return result;
	}

	/**
	 * Conversion en chaine de caracteres pour l'affichage.
	 *  @return Document converti en chaine de caracteres
	 */
	@Override
	public String toString() {
		String s = "\"" + code + "\" " + titre + " " + auteur + " " + annee
				+ " " + genre + " " + localisation + " " + nbEmprunts;
		if (empruntable) {
			s += " (emp ";
			if (emprunte) {
				s += "O";
			} else {
				s += "N";
			}
			s += ")";
		}
		if (invariant()) {
			s += " SAFE ";
		} else {
			s += " UNSAFE ";
		}
		return s;
	}

	/**
	 * Autorise l'emprunt du document.
	 * @exception OperationImpossible si document non empruntable
	 * @exception InvariantBroken si document non invariant après opération.
	 */
	public final void metEmpruntable() 
			throws OperationImpossible, InvariantBroken {
		if (empruntable) {
			throw new OperationImpossible("Document metEmpruntable empruntable"
					+ this);
		}
		empruntable = true;
		if (!invariant()) {
			throw new InvariantBroken("Document -" + this);
		}
	}

	/**
	 * Interdit l'emprunt du document.
	 * @exception OperationImpossible si document déja consultable
	 * @exception InvariantBroken si document non invariant après operation.
	 */
	public final void metConsultable() 
			throws OperationImpossible, InvariantBroken {
		if (!empruntable) {
			throw new OperationImpossible("Document metConsultable consultable"
					+ this);
		}
		if (emprunte) {
			throw new OperationImpossible("Document metConsultable emprunte"
					+ this);
		}
		empruntable = false;
		if (!invariant()) {
			throw new InvariantBroken("Document -" + this);
		}
	}

	/**
	 * Retourne vrai si le document est empruntable.
	 * @return true si document empruntable
	 */
	public final boolean estEmpruntable() { return empruntable; }

	// Operations du DME
	/**
	 * <TT>emprunter</TT> est appelee lors de l'emprunt d'un document.
	 * Les statistiques sont mises a jour.
	 * @exception OperationImpossible si document déja emprunte
	 * @exception InvariantBroken si document non invariant après operation. 
	 * @return true si emprunt Ok exception sinon
	 */
	public boolean emprunter() throws InvariantBroken, OperationImpossible {
		if (!empruntable) {
			throw new OperationImpossible("Document non empruntable" + this);
		}
		if (emprunte) {
			throw new OperationImpossible("Deja Emprunte" + this);
		}
		emprunte = true;
		genre.emprunter();
		nbEmprunts++;
		// and check after
		if (!invariant()) {
			throw new InvariantBroken("Document -" + this);
		}
		return true;
	}

	/**
	 * Retourne vrai si le document est emprunte.
	 * @return true si document emprunte
	 */
	public final boolean estEmprunte() { return emprunte; }

	/**
	 * <TT>restituer</TT> est appelee lors de la restitution d'un
	 * document. La localisation ou ranger le document est affichee.
	 * @exception OperationImpossible si document déja emprunte
	 * @exception InvariantBroken si document non invariant après operation.
	 */
	public void restituer() throws InvariantBroken, OperationImpossible {
		if (!empruntable) {
			throw new OperationImpossible(
					"Impossible de restituer un document non empruntable");
		}
		if (!emprunte) {
			throw new OperationImpossible(
					"Impossible de restituer un document non emprunte");
		}
		emprunte = false;
		// check invariant after modifying internal state
		if (!invariant()) {
			throw new InvariantBroken("Document -" + this);
		}
		System.out.println("Document: ranger \"" + titre + "\" en "
				+ localisation);
	}

	/**
	 * <TT>afficherStatDocument</TT> affiche les statistiques d'emprunt
	 * du document.
	 */
	public final void afficherStatDocument() {
		System.out.println("(stat) Nombre d'emprunts du document \"" + titre
				+ "\" de \"" + auteur + "\" (" + code + ") = " + nbEmprunts);
	}

	/**
	 * Safety property - emprunte => empruntable.
	 * @return if the document is in a safe state, i.e respects the invariant
	 */
	public final boolean invariant() {
		return !(emprunte && !empruntable);
	}
}
