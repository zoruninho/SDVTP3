package mediatheque;

import java.io.Serializable;
import java.util.Date;

import mediatheque.client.Client;
import mediatheque.document.Document;
import util.Datutil;
import util.InvariantBroken;

/**
 * La classe FicheEmprunt gere les fiches d'emprunts de la mediatheque.
 * Il y a une fiche par document emprunte et par client emprunteur.
 */
public final class FicheEmprunt implements Serializable {
	/** serial version. */
	private static final long serialVersionUID = 3L;
	// Associations
	/** Emprunteur. */
	private Client client;
	/** Document emprunte. */
	private Document document;

	// Attributs
	/** Date de l'emprunt.	 */
	private Date dateEmprunt;
	/** Date limite de restitution.	 */
	private Date dateLimite;
	/** Indicateur d'emprunt depasse. */
	private boolean depasse;
	/** date de rappel si emprunt depasse.  */
	private Date dateRappel;

	/**
	 * Nombre d'emprunts total de documents de la mediatheque.
	 */
	private static int nbEmpruntsTotal = 0;

	// Les methodes
	/**
	 * Constructeur.
	 * @param d document associe
	 * @param c client associe
	 * @throws OperationImpossible en relai de emprunter sur document et client
	 * @throws InvariantBroken en relai de emprunter sur document
	 */
	public FicheEmprunt(final Client c, final Document d)
			throws OperationImpossible, InvariantBroken {
		client = c;
		document = d;
		dateEmprunt = Datutil.dateDuJour();
		int duree = document.dureeEmprunt();
		dateLimite = client.dateRetour(dateEmprunt, duree);
		depasse = false;
		document.emprunter();
		client.emprunter(this);
		nbEmpruntsTotal++;
		System.out.println("\tTarif = " + getTarifEmprunt() + " euros");
	}

	/**
	 * <TT>verifier</TT> teste si la date de fin de prêt est depassée.
	 * @return booleen si depasse pour la première fois.
	 */
	public boolean verifier() {
		if (depasse) {
			return false;
		} else {
			Date dateActuelle = Datutil.dateDuJour();
			if (dateLimite.before(dateActuelle)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Le client est marque ; la lettre de rappel est cree.
	 * @return booleen egal a depasse
	 * @throws InvariantBroken en relai de client.marquer().
	 */
	public boolean premierRappel() throws InvariantBroken {
		if(!depasse) {
			depasse = true;
			client.marquer();
			dateRappel = Datutil.dateDuJour();
		}
		return depasse;
	}

	/**
	 * <TT>relancer</TT> verifie si l'emprunt est depasse, auquel cas
	 * il faudra relancer le client retardataire.
	 * @return true si changement de date de relance false sinon
	 */
	public boolean relancer() {
		Date dateActuelle = Datutil.dateDuJour();
		if (depasse) {
			Date dateRelance = Datutil.addDate(dateRappel, 
					Datutil.DAYSINWEEK);
			if (dateRelance.before(dateActuelle)) {
				dateRappel = dateActuelle;
				return true;
			}
		}
		return false;
	}

	/**
	 * modifie le client associe a l'emprunt pour permettre les modifications
	 * de nom et prenom dans la hashtable.
	 * @param newClient 
	 */
	void modifierClient(final Client newClient) {
		client = newClient;
	}

	/**
	 * <TT>correspond</TT> verifie que l'emprunt correspond au document et
	 * au client en retournant vrai.
	 *   @param cli Emprunteur
	 *   @param doc Document emprunte
	 *   @return true si l'emprunt correspond
	 */
	public boolean correspond(final Client cli, final Document doc) {
		return (client.equals(cli) && document.equals(doc));
	}

	/**
	 * <TT>restituer</TT> est lancee lors de la restitution d'un document.
	 * Elle appelle les methodes de restitution sur le document et le client.
	 * @throws InvariantBroken en relai de restituer sur document
	 * @throws OperationImpossible en relai de restituer sur document et client.
	 */
	public void restituer() throws InvariantBroken, OperationImpossible {
		client.restituer(this);
		document.restituer();
	}
	/**
	 * retourne le client associe a la fiche.
	 * @return client
	 */
	public Client getClient() {
		return client;
	}
	/**
	 * retourne le document associe à la fiche.
	 * @return document
	 */
	public Document getDocument() {
		return document;
	}
	/**
	 * retourne la date d'emprunt.
	 * @return date d'emprunt
	 */
	public Date getDateEmprunt() {
		return dateEmprunt;
	}
	/**
	 * retourne la date limite.
	 * @return date limite
	 */
	public Date getDateLimite() {
		return dateLimite;
	}
	/**
	 * retourne la valeur du booleen depasse.
	 * @return depasse
	 */
	public boolean getDepasse() {
		return depasse;
	}
	/**
	 * retourne la duree d'emprunt en jour.
	 * @return duree d'emprunt
	 */
	public int getDureeEmprunt() {
		return (int) Math.round(((dateLimite.getTime() - dateEmprunt.getTime()) 
				/ ((double)Datutil.MILLISINSEC * Datutil.SECSINMIN 
						* Datutil.MINSINHOUR * Datutil.HOURSINDAY)));

	}
	/**
	 * retourne le tarif d'emprunt calcule à partir du tarifnominal du 
	 * document et du type de client.
	 * @return tarif
	 */
	public double getTarifEmprunt() { 
		double tarifNominal = document.tarifEmprunt();
		return  client.sommeDue(tarifNominal);
	}

	/**
	 * changementCategorie est appele apres un changement de 
	 *  categorie du client calcule si l'emprunt est en retard ou non.
	 * @return boolean true si l'emprunt etait depasse
	 * @throws OperationImpossible en relai de verifier
	 * @throws InvariantBroken en relai de verifier
	 */
	public boolean changementCategorie() throws InvariantBroken, OperationImpossible {
		boolean oldDepasse = depasse;
		if (depasse) {
			depasse = false;
		}
		int duree = document.dureeEmprunt();
		dateLimite = client.dateRetour(dateEmprunt, duree);
		verifier();
		return oldDepasse;
	}

	/**
	 *<TT>toString</TT> affiche les caracteristiques de l'emprunt.
	 *  @return Caracteristiques de l'emprunt
	 */
	@Override
	public String toString() {
		String s = "\"" + document.getCode() + "\" par \"" + client.getNom()
				+ "\" le " + Datutil.dateToString(dateEmprunt) + " pour le "
				+ Datutil.dateToString(dateLimite);
		if (depasse) {
			s = s + " (depasse)";
		}
		return s;
	}

	/**
	 *<TT>afficherStatistiques</TT> affiche le nombre total d'emprunts.
	 */
	public static void afficherStatistiques() {
		System.out.println("Nombre total d'emprunts = " + nbEmpruntsTotal);
	}
}
