package mediatheque;

import java.io.Serializable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;

import mediatheque.client.Client;
import mediatheque.client.CategorieClient;
import mediatheque.client.HashClient;
import mediatheque.document.Audio;
import mediatheque.document.Document;
import mediatheque.document.Livre;
import mediatheque.document.Video;
import util.Datutil;
import util.InvariantBroken;

/**
 * La classe <code>Mediatheque</code> gere l'interface du systeme de
 * gestion de la mediatheque. Les principales fonctions sont :
 * <ul>
 * <li>ajouts et suppressions des clients et documents
 * <li>emprunts et restitution des documents empruntes
 * <li>verification et relance des clients
 * </ul>
 * Dans cette version, les donnees sont permanentes.
 */
public final class Mediatheque implements Serializable {
	/** serial number.	 */
	private static final long serialVersionUID = 3L;

	/**
	 * Nom de la mediatheque, format libre.
	 */
	private String nomMedia;
	/* Objets geres sur le modèle des listes. 	 */
	/** liste des genres. */	
	private List<Genre> lesGenres;
	/** liste des localisations. */
	private List<Localisation> lesLocalisations;
	/** liste des categories client. */
	private List<CategorieClient> lesCatsClient;
	/** liste des fiches d'emprunt.   */	
	private List<FicheEmprunt> lesEmprunts;

	/* Objets gérés sur le modèles des tables de hachage. */
	/** Objets geres par la mediatheque : documents. */	
	private Map<String, Document> lesDocuments;
	/** Objets geres par la mediatheque : Client. */
	private Map<HashClient, Client> lesClients;

	/** boolean pour debuger la classe par des affichages.	 */
	private static boolean debug = false;

	/**
	 * Constructeur de la mediatheque qui initialise les listes
	 * de clients, de documents et d'emprunts. Dans cette version,
	 * ces listes sont initialisees a vide a chaque lancement.
	 *    @param nom Nom de la mediatheque
	 */
	public Mediatheque(final String nom) {
		this.nomMedia = nom;
		if (debug) {
			System.out.println("Mediatheque \"" + nom + "\"");
		}
		empty();
		initFromFile();
		try {
			verifier();
		} catch (InvariantBroken ib) {
			System.out.println("Donnees mediatheque incoherentes"
					+ "reinitialisez le fichier" + nom + ".data");
		}
	}

	/**
	 * Initialisation des collections a vide.
	 */
	public void empty() {
		lesGenres = new Vector<Genre>();
		lesLocalisations = new Vector<Localisation>();
		lesDocuments = new Hashtable<String, Document>();
		lesClients = new Hashtable<HashClient, Client>();
		lesEmprunts = new Vector<FicheEmprunt>();
		lesCatsClient = new Vector<CategorieClient>();
	}

	// Methodes pour manipuler les genres
	// chercher, ajouter, supprimer, lister
	// + methodes pour l'interface graphique taille du vecteur et elementAt
	/**
	 * chercherGenre cherche un Genre dans la liste des genres.
	 *    @param nomGenre du Genre a chercher
	 *    @return le genre correspondant au nom dans la collection
	 */
	public Genre chercherGenre(final String nomGenre) {
		Genre searched = new Genre(nomGenre);
		int index = lesGenres.indexOf(searched);
		if (index >= 0) {
			return lesGenres.get(index);
		} else {
			return null;
		}
	}

	/**
	 * supprimerGenre< permet de supprimer un Genre dans la
	 * liste des genres.
	 *    @param n du Genre a supprimer
	 *    @exception OperationImpossible genre inexistant
	 */
	public void supprimerGenre(final String n) throws OperationImpossible {
		if (debug) {
			System.out.println("Mediatheque: suppression d'un genre.");
			System.out.println("\t" + n);
		}
		Genre g = chercherGenre(n);
		if (g == null) {
			throw new OperationImpossible("Genre " + n + " inexistant");
		} else {
			if (existeDocument(g)) {
				throw new OperationImpossible("Suppression de genre impossible. "
						+ "Il existe au moins un document associe au genre " + g);
			}
			if (lesGenres.remove(g)) {
				if (debug) {
					System.out.println("Mediatheque: Genre \"" + n + "\" retire");
				}
			} else {
				throw new OperationImpossible("Genre " + n + " inexistant");
			}
		}
	}

	/**
	 * ajouterGenre< permet d'ajouter un Genre dans la 
	 * liste des genres.
	 *    @param n du Genre a ajouter
	 *    @exception OperationImpossible genre deja present
	 */
	public void ajouterGenre(final String n) throws OperationImpossible {
		if (debug) {
			System.out.println("Mediatheque: ajouter un genre.");
			System.out.println("\t" + n);
		}		
		Genre g = chercherGenre(n);
		if (g == null) {
			lesGenres.add(new Genre(n));
		} else {
			throw new OperationImpossible("ajouter Genre existant:" + n);
		}
	}

	/**
	 * modifierGenre permet de modifier un Genre. 
	 *    @param old ancien nom du Genre
	 *    @param neuf nouveau nom du Genre
	 *    @exception OperationImpossible genre deja present
	 */
	public void modifierGenre(final String old, final String neuf) 
			throws OperationImpossible {
		Genre g = chercherGenre(old);
		if (g == null) {
			throw new OperationImpossible("Genre \""
					+ old + "\" inexistant");
		} else {
			g.modifier(neuf);
		}
	}
	/**
	 * listerGenres permet de lister les Genre.
	 */ 
	public void listerGenres() {
		if (debug) {
			System.out.println("Mediatheque " + nomMedia 
					+ "  listage des genres au " 
					+ Datutil.dateToString(Datutil.dateDuJour()));
		}
		for (Genre g : lesGenres) {
			System.out.println(g);
		}
	}
	/**
	 * pour l'interface graphique permet d'obtenir le ieme Genre.
	 * @param n rang dans la collection
	 * @return le ieme Genre dans la collection des genres
	 */
	public Genre getGenreAt(final int n) {
		return lesGenres.get(n);
	}
	/**
	 * pour l'interface graphique permet d'obtenir le nombre de Genre.
	 * @return la taille de la collection des genres
	 */
	public int getGenresSize() {
		return lesGenres.size();
	}

	// Methodes pour manipuler les localisations
	// chercher, ajouter, supprimer, lister
	/**
	 * supprimerLocalisation permet de supprimer une Localisation
	 *  dans la liste des Localisations.
	 *    @param salle nom de la salle
	 *    @param rayon nom du rayon
	 *    @exception OperationImpossible localisation inexistante
	 */
	public void supprimerLocalisation(final String salle, final String rayon)
			throws OperationImpossible {
		if (debug) {
			System.out.println("Mediatheque: suppression d'une localisation.");
			System.out.println("\t" + salle + "\t" + rayon);
		}
		Localisation l = chercherLocalisation(salle, rayon);
		if (l == null) {
			throw new OperationImpossible("Localisation " + salle + " " 
					+ rayon + " inexistant");
		} else {
			if (existeDocument(l)) {
				throw new OperationImpossible("Suppression de localisation" 
						+ " impossible. Il existe au"
						+ " moins un document a la localisation " + l);
			}
			if (lesLocalisations.remove(l)) {
				if (debug) {
					System.out.println("Mediatheque: Localisation \"" + salle + "/"
							+ rayon + "\" retiree");
				}
			} else {
				throw new OperationImpossible("Localisation " + salle + " " + rayon
						+ " inexistant");

			}
		}
	}
	/**
	 * chercherLocalisation cherche une localisation dans la
	 * liste des localisations.
	 *    @param salle nom de la salle
	 *    @param rayon nom du rayon
	 *    @return localisation ou null
	 */
	public Localisation chercherLocalisation(final String salle, 
			final String rayon) {
		Localisation l = new Localisation(salle, rayon);
		int index = lesLocalisations.indexOf(l);
		if (index == -1) {
			return null;
		}
		return lesLocalisations.get(index);
	}
	/**
	 * ajouterLocalisation permet d'ajouter une localisation dans la 
	 * liste.
	 *    @param s salle
	 *    @param r rayon
	 *    @exception OperationImpossible localisation existante
	 */
	public void ajouterLocalisation(final String s, final String r)
			throws OperationImpossible {
		if (debug) {
			System.out.println("Localisation: ajouter une localisation.");
			System.out.println("\t" + s + " " + r);
		}		
		if (chercherLocalisation(s, r) != null) {
			throw new OperationImpossible("Localisation \""
					+ s + " " + r + "\" deja existant");
		} else {
			lesLocalisations.add(new Localisation(s, r));
		}
	}
	/**
	 * modifierLocalisation modifie la salle et le rayon d'une localisation.
	 * @param loc la localisation a modifier
	 * @param s nouvelle salle
	 * @param r nouveau rayon
	 * @exception OperationImpossible localisation n'est pas dans la collection.
	 */
	public void modifierLocalisation(final Localisation loc, final String s, 
			final String r) throws OperationImpossible {
		Localisation inVector = chercherLocalisation(loc.getSalle(), 
				loc.getRayon());
		if (inVector == null) {
			throw new OperationImpossible("Modifier Localisation inexistante");
		}
		if (!inVector.getSalle().equals(s)) {
			inVector.setSalle(s);
		}
		if (!inVector.getRayon().equals(r)) {
			inVector.setRayon(r);
		}
	}
	/**
	 * permet d'afficher toutes les localisations.
	 */
	public void listerLocalisations() {
		System.out.println("Mediatheque " + nomMedia
				+ "  listage des localisations au "
				+ Datutil.dateToString(Datutil.dateDuJour()));
		Localisation l = null;
		for (int i = 0; i < lesLocalisations.size(); i++) {
			l = (Localisation) lesLocalisations.get(i);
			System.out.println(l);
		}
	}
	/**
	 * retourne la localisation correspondant au rang.
	 * @param n rang dans le vecteur 
	 * @return la localisation correspondant au rang
	 */
	public Localisation getLocalisationAt(final int n) {
		return lesLocalisations.get(n);
	}
	/**
	 * retourne la taille de la collection des localisations.
	 * @return taille de la collection des localisations
	 */
	public int getLocalisationsSize() {
		return lesLocalisations.size();
	}

	// Methodes pour manipuler les categories de client
	// chercher, ajouter, supprimer, lister + interface graphique
	/**
	 * cherche une categorie de client dans la
	 * collection des categories.
	 *    @param catName nom de categorie recherchee
	 *    @return la categorie de la collection qui correspond au parametre
	 */
	public CategorieClient chercherCatClient(final String catName) {
		CategorieClient searched = new CategorieClient(catName);
		int index = lesCatsClient.indexOf(searched);
		if (index >= 0) {
			return lesCatsClient.get(index);
		} else {
			return null;
		}
	}

	/**
	 * permet de supprimer une categorie dans la
	 * collection des categories.
	 *    @param catName nom du categorie a supprimer
	 *    @throws OperationImpossible categorie inexistante, 
	 *    ou client dans cette categorie
	 */
	public void supprimerCatClient(final String catName) 
			throws OperationImpossible {
		if (debug) {
			System.out.println("Mediatheque: suppression d'une categorie.");
			System.out.println("\t" + catName);
		}
		CategorieClient c = chercherCatClient(catName);
		if (c == null) {
			throw new OperationImpossible("Categorie " + catName 
					+ " inexistante");
		} else {
			if (existeClient(c)) {
				throw new OperationImpossible(
						"Il existe un client dans la categorie " + catName);
			}
			if (lesCatsClient.remove(c)) {
				if (debug) {
					System.out.println("Mediatheque: Categorie \"" + catName 
							+ "\" retire");
				}
			} else {
				throw new OperationImpossible("Categorie " + catName 
						+ " inexistante");
			}
		}
	}

	/**
	 * permet d ajouter une categorie dans la
	 * collection des categories.
	 *    @param name nom de la categorie a ajouter
	 *    @param max nombre maximum d'emprunt
	 * @param cot cotisation
	 * @param coefDuree coefficient de duree
	 * @param coefTarif coefficient de tarif
	 * @param codeReducUsed categorie avec code de reduction
	 * @return reference sur la categorie ajoutee
	 * @throws OperationImpossible categorie existe déjà
	 */

	public CategorieClient ajouterCatClient(final String name, final int max, 
			final double cot, final double coefDuree, final double coefTarif, 
			final boolean codeReducUsed) 
					throws OperationImpossible {
		if (debug) {
			System.out.println("Mediatheque: ajouter un categorie de client.");
			System.out.println("\t" + name);
		}
		CategorieClient c = chercherCatClient(name);
		if (c != null) {
			throw new OperationImpossible("Categorie client \""
					+ c + "\" deja existant");
		} else {
			c = new CategorieClient(name, max, cot, coefDuree, coefTarif, 
					codeReducUsed);
			lesCatsClient.add(c);
		}
		return c;
	}

	/**
	 * permet de modifier une categorie dans la
	 * collection des categories.
	 * @param co categorie a modifier
	 * @param name nouveau nom
	 * @param max nombre maximum d'emprunts
	 * @param cot cotisation
	 * @param coefDuree coefficient de duree
	 * @param coefTarif coefficient sur le tarif
	 * @param codeReducUsed code de reduction utilise
	 * @return CategorieClient la categorie modifiee
	 * @throws OperationImpossible categorie inexistante
	 */
	public CategorieClient modifierCatClient(final CategorieClient co, 
			final String name, final int max, final double cot, 
			final double coefDuree,
			final double coefTarif, final boolean codeReducUsed)
					throws OperationImpossible {
		CategorieClient c = chercherCatClient(co.getNom());
		if (c == null) {
			throw new OperationImpossible("Categorie client \""
					+ co.getNom() + "\" inexistante");
		} else {
			if (!co.getNom().equals(name)) {
				co.modifierNom(name);
			}
			if (co.getNbEmpruntMax() != max) {
				co.modifierMax(max);
			}
			if (co.getCotisation() != cot) {
				co.modifierCotisation(cot);
			}
			if (co.getCoefDuree() != coefDuree) {
				co.modifierCoefDuree(coefDuree);
			}
			if (co.getCoefTarif() != coefTarif) {
				co.modifierCoefTarif(coefTarif);
			}
			if (co.getCodeReducUtilise() != codeReducUsed) {
				co.modifierCodeReducActif(codeReducUsed);
			}
		}
		return c;
	}
	/**
	 * liste les categories de client.
	 */
	public void listerCatsClient() {
		if (debug) {
			System.out.println("Mediatheque " + nomMedia 
					+ "  listage des categories de clients " 
					+ Datutil.dateToString(Datutil.dateDuJour()));
		}
		for (CategorieClient c : lesCatsClient) {
			System.out.println(c);
		}
	}
	/**
	 * retourne la categorie de la collection correspondant au rang.
	 * @param n rang dans la collection
	 * @return la categorie.
	 */
	public CategorieClient getCategorieAt(final int n) {
		return lesCatsClient.get(n);
	}
	/**
	 * retourne la taille de la collection des categories.
	 * @return taille de la collection.
	 */
	public int getCategoriesSize() {
		return lesCatsClient.size();
	}
	// Methodes sur les documents :
	// chercherDocument, ajouterDocument, retirer, metEmpruntable,
	// metConsultable
	/**
	 * cherche le document dont le code est
	 * indique en parametre.
	 *    @param code Code du document a chercher
	 *    @return Le document ou <code>null</code> en cas d'echec
	 */
	public Document chercherDocument(final String code) {
		return lesDocuments.get(code);
	}
	/**
	 * permet d'ajouter un document dans le
	 * fond de la mediatheque.
	 *    @param doc Document a ajouter
	 *    @throws OperationImpossible Code deja attribue
	 */
	public void ajouterDocument(final Document doc) throws OperationImpossible {
		if (debug) {
			System.out.println("Mediatheque: ajouter un document.");
			System.out.println("\t" + doc.getCode() + " \"" + doc.getTitre() 
					+ "\" de " + doc.getAuteur());
		}
		if (lesDocuments.containsKey(doc.getCode())) {
			throw new OperationImpossible("Document \"" + doc.getCode() 
					+ "\" deja existant");
		} else {
			boolean g = lesGenres.contains(doc.getGenre());
			if (!g) {
				throw new OperationImpossible("Ajout d'un document avec un " 
						+ "genre non inclus dans la mediatheque");
			}
			boolean l = lesLocalisations.contains(doc.getLocalisation());
			if (!l) {
				throw new OperationImpossible("Ajout d'un document avec une " 
						+ "localisation inexistante");
			}
			lesDocuments.put(doc.getCode(), doc);
		}
	}
	/**
	 * est appelee pour retirer un document
	 * de la mediatheque donne par son code suppose unique.
	 * L'exception <TT>OperationImpossible</TT> est levee si le document
	 * est emprunte ou si le document n'appartient pas a la mediatheque.
	 *    @param code Code du document a retirer
	 *    @throws OperationImpossible En cas d'erreur (voir ci-dessus)
	 */
	public void retirerDocument(final String code) 
			throws OperationImpossible {
		if (lesDocuments.containsKey(code)) {
			Document doc = lesDocuments.get(code);
			if (doc.estEmprunte()) {
				throw new OperationImpossible("Document \"" 
						+ code + "\" emprunte");
			}
			lesDocuments.remove(code);
		} else {
			throw new OperationImpossible("Document " + code + " inexistant");
		}
	}
	/**
	 *  Autorise l'emprunt du document.
	 *  @param code Code du document a rendre empruntable
	 *  @exception OperationImpossible Document inexistant
	 *  @exception InvariantBroken relai l'exception levée par metEmpruntable. 
	 */
	public void metEmpruntable(final String code)
			throws OperationImpossible, InvariantBroken {
		Document doc = chercherDocument(code);
		if (doc == null) {
			throw new OperationImpossible("MetEmpruntable code inexistant:"
					+ code);
		}
		doc.metEmpruntable();
	}

	/**
	 *  Interdit l'emprunt du document.
	 *	@param code Code du document a rendre consultable
	 *	@exception OperationImpossible Document inexistant
	 *  @exception InvariantBroken relai l'exception levée par metConsulatble
	 */
	public void metConsultable(final String code)
			throws OperationImpossible, InvariantBroken {
		Document doc = chercherDocument(code);
		if (doc == null) {
			throw new OperationImpossible("MetConsultable code inexistant:"
					+ code);
		}
		doc.metConsultable();
	}

	/**
	 * affiche les documents en cours.
	 */
	public void listerDocuments() {
		if (debug) {
			System.out.println("Mediatheque " + nomMedia 
					+ "  listage des documents au " 
					+ Datutil.dateToString(Datutil.dateDuJour()));
		}
		if (lesDocuments.isEmpty()) {
			System.out.println("(neant)");
		} else {
			for (Document d : lesDocuments.values()) {
				System.out.println(d);
			}
		}
	}

	/**
	 *  cherche un document dont le genre est
	 * indique au parametre.
	 *    @param g Genre du document a chercher
	 *    @return true s'il en existe un false sinon
	 */
	private boolean existeDocument(final Genre g) {
		Collection<Document> e = lesDocuments.values();
		for (Document d : e) {
			if (d.getGenre().equals(g)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * cherche un document dont la localisation est
	 * indique au parametre.
	 *    @param l Localisation du document a chercher
	 *    @return true s'il en existe un false sinon
	 */
	private boolean existeDocument(final Localisation l) {
		Collection<Document> e = lesDocuments.values();
		for (Document d : e) {
			if (d.getLocalisation().equals(l)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * retourne le neme document de la collection.
	 * @param n rang du document.
	 * @return le document correspondant.
	 */
	public Document getDocumentAt(final int n) {
		if (n >= lesDocuments.size()) {
			return null;
		}
		Collection<Document> e = lesDocuments.values();

		Iterator<Document> iter = e.iterator();
		int i;
		Document d = null;
		for (i = 0; i <= n; i++) {
			if (iter.hasNext()) {
				d = iter.next();
			} else {
				return null;
			}
		}
		return d;
	}
	/**
	 * retourne le nombre de documents dans la collection.
	 * @return nombre de documents
	 */
	public int getDocumentsSize() {
		return lesDocuments.size();
	}
	// Methodes qui concernent les fiches d'emprunts
	// emprunter, restituer, verifier

	/**
	 * <TT>emprunter</TT> est appelee pour l'emprunt d'un document par un
	 * client. L'exception <TT>OperationImpossible</TT> est levee si le
	 * document n'existe pas, n'est pas empruntable ou s'il est deja
	 * emprunte, ou si le client n'existe pas ou ne peut pas emprunter.
	 * Le tarif du pret est retourne.
	 *    @param nom Nom du client emprunteur
	 *    @param prenom Prenom du client emprunteur
	 *    @param code Code du document a emprunter
	 *    @exception OperationImpossible voir cas ci-dessus
	 *    @exception InvariantBroken relai de l'exception en provenance du
	 *    constructeur de la fiche d'emprunt.
	 */
	public void emprunter(final String nom, final String prenom, 
			final String code)
					throws OperationImpossible, InvariantBroken {
		Client client = chercherClient(nom, prenom);
		if (client == null) {
			throw new OperationImpossible("Client " + nom + " " + prenom
					+ " inexistant");
		}
		if (!client.peutEmprunter()) {
			throw new OperationImpossible("Client " + client.getNom()
					+ " non autorise a emprunter");
		}
		Document doc = chercherDocument(code);

		if (doc == null) {
			throw new OperationImpossible("Document " + code + " inexistant");
		}
		if (!doc.estEmpruntable()) {
			throw new OperationImpossible("Document " + doc.getCode()
					+ " non empruntable");
		}
		if (doc.estEmprunte()) {
			throw new OperationImpossible("Document " + doc.getCode()
					+ " deja emprunte");
		}
		FicheEmprunt emprunt = new FicheEmprunt(client, doc);
		lesEmprunts.add(emprunt);
		return;
	}

	/**
	 * <TT>restituer</TT> est lancee lors de la restitution d'un ouvrage.
	 * Elle appelle la methode de restitution sur l'emprunt. L'exception
	 * <TT>OperationImpossible</TT> est levee si le client ou le
	 * document n'existent pas.
	 *    @param nom Nom du client emprunteur
	 *    @param prenom Prenom du client emprunteur
	 *    @param code Code du document a restituer
	 *    @exception OperationImpossible Restitution impossible
	 *    @exception InvariantBroken relai du a emprunt.restituer()
	 */
	public void restituer(final String nom, final String prenom, 
			final String code)
					throws OperationImpossible, InvariantBroken {
		Client client = chercherClient(nom, prenom);
		if (client == null) {
			throw new OperationImpossible("Client " + nom + " " + prenom
					+ " inexistant");
		}
		Document doc = chercherDocument(code);
		if (doc == null) {
			throw new OperationImpossible("Document " + code + " inexistant");
		}
		for (int i = 0; i < lesEmprunts.size(); i++) {
			FicheEmprunt emprunt = lesEmprunts.get(i);
			if (emprunt.correspond(client, doc)) {
				emprunt.restituer();
				lesEmprunts.remove(i);
				return;
			}
		}
		throw new OperationImpossible("Emprunt par \"" + nom + "\" de \""
				+ code + "\" non trouve");
	}

	/**
	 * verifier est lancee chaque jour afin de determiner
	 * les emprunts non restitues dans les delais.
	 * Chaque emprunt detecte depasse pour la premiere fois provoque
	 * l'impression d'une lettre de rappel au client.
	 * @throws InvariantBroken relai de la methode verifier
	 */
	public void verifier() throws InvariantBroken {
		if (debug) {
			System.out.println("Mediatheque: verification le " 
					+ 	Datutil.dateToString(Datutil.dateDuJour()));
		}
		for (FicheEmprunt emprunt : lesEmprunts) {
			if(emprunt.getDepasse()){
				emprunt.relancer();
			} else {
				if (emprunt.verifier()) {
					emprunt.premierRappel();
				}
			}
		}
	}

	/**
	 * affiche les emprunts en cours.
	 */
	public void listerFicheEmprunts() {
		if (debug) {
			System.out.println("Mediatheque " + nomMedia 
					+ "  listage des empruts au " 
					+ Datutil.dateToString(Datutil.dateDuJour()));
		}
		if (lesEmprunts.size() == 0) {
			System.out.println("(neant)");
		} else {
			for (FicheEmprunt emprunt : lesEmprunts) {
				System.out.println(emprunt);
			}
		}
	}
	/**
	 * fiche d'emprunt correspondant au rang.
	 * @param n rang
	 * @return la fiche de la collections
	 */
	public FicheEmprunt getFicheEmpruntAt(final int n) {
		return lesEmprunts.get(n);
	}
	/**
	 * taille de la collection des fiches d'emprunts.
	 * @return nombre d'emprunts en cours.
	 */
	public int getFicheEmpruntsSize() {
		return lesEmprunts.size();
	}

	// Methodes sur les clients
	// inscrire, resilier, chercher, lister
	/**
	 * Inscription d'un client dans la mediatheque. L'exception
	 * <TT>OperationImpossible</TT> est levee si le client existe
	 * deja dans la mediatheque ou si la categorie du client n'existe pas ou
	 * si la categorie du client necessite un code de reduction
	 * @param nom nom du client
	 * @param prenom preneom du client
	 * @param adresse adresse
	 * @param nomcat nom de la categorie du client
	 * @return double tarif pour ce client
	 * @exception OperationImpossible en cas d'erreur (voir ci-dessus)
	 */
	public double inscrire(final String nom, final String prenom, 
			final String adresse, 
			final String nomcat) throws OperationImpossible {

		CategorieClient c = chercherCatClient(nomcat);
		if (c == null) {
			throw new OperationImpossible("Pas de categorie client " + nomcat);
		}
		return inscrire(nom, prenom, adresse, c, 0);
	}
	/**
	 * Inscription d'un client dans la mediatheque. L'exception
	 * <TT>OperationImpossible</TT> est levee si le client existe
	 * deja dans la mediatheque.
	 * @param nom nom du client
	 * @param prenom prenom du client
	 * @param adresse adresse du client
	 * @param nomcat nom de la categorie du client
	 * @param code code de reduction de ce client
	 * @return double tarif pour ce client
	 * @exception OperationImpossible en cas d'erreur (voir ci-dessus)
	 */
	public double inscrire(final String nom, final String prenom, 
			final String adresse, 
			final String nomcat, final int code)
					throws OperationImpossible {
		CategorieClient c = chercherCatClient(nomcat);
		if (c == null) {
			throw new OperationImpossible("Pas de categorie client " + nomcat);
		}
		return inscrire(nom, prenom, adresse, c, code);
	}

	/**
	 * Inscription d'un client dans la mediatheque. L'exception
	 * <TT>OperationImpossible</TT> est levee si le client existe
	 * deja dans la mediatheque.
	 * @param nom nom du client
	 * @param prenom prenom du client
	 * @param adresse adresse du client
	 * @param cat categorie du client
	 * @param code code de reduction de ce client
	 * @return double tarif pour ce client
	 * @exception OperationImpossible en cas d'erreur (voir ci-dessus)
	 */
	public double inscrire(final String nom, final String prenom, 
			final String adresse, 
			final CategorieClient cat, final int code) 
					throws OperationImpossible {

		double tarif = 0.0;
		if (debug) {
			System.out.println("Mediatheque: inscription de " + nom
					+ " " + prenom);
		}
		HashClient hc = new HashClient(nom, prenom);

		if (lesClients.containsKey(hc)) {
			throw new OperationImpossible("Client " + nom + " " + prenom
					+ " deja existant");
		} else {
			Client client;
			if (cat.getCodeReducUtilise()) {
				client = new Client(nom, prenom, adresse, cat, code);
			} else {
				client = new Client(nom, prenom, adresse, cat);
			}
			tarif = cat.getCotisation();
			lesClients.put(hc, client);
		}
		return tarif;
	}
	/**
	 * <TT>resilier()</TT> est appelee pour retirer un client
	 * de la mediatheque. L'exception <TT>OperationImpossible</TT> est levee
	 * si le client n'appartient pas a la mediatheque ou s'il n'a pas
	 * restitue tous ses documents empruntes.
	 *   @param nom Nom du client
	 *   @param prenom Prenom du client
	 *   @exception OperationImpossible En cas d'erreur (voir ci-dessus)
	 */
	public void resilier(final String nom, final String prenom)
			throws OperationImpossible {
		HashClient hc = new HashClient(nom, prenom);
		Client client = null;
		if (lesClients.containsKey(hc)) {
			client = lesClients.get(hc);
		} else {
			throw new OperationImpossible("Client " + nom + " " + prenom
					+ " inexistant");
		}
		if (client.aDesEmpruntsEnCours()) {
			throw new OperationImpossible("Client " + nom + " " + prenom
					+ " n'a pas restitue tous ses emprunts");
		}
		lesClients.remove(hc);
		if (debug) {
			System.out.println("Mediatheque: desinscrire le client \"" 
					+ nom + " " + prenom + "\".");
		}
		client.afficherStatCli();
	}
	/**
	 * Modifier les informations d'un client. Nom, ou prénom, 
	 * ou adresse ou categorie.
	 * @param client a modifier
	 * @param nom ancien ou nouveau nom.
	 * @param prenom ancien ou nouveau prenom.
	 * @param adresse ancienne ou nouvelle adresse.
	 * @param catnom categorie.
	 * @param code reduction.
	 * @throws OperationImpossible client inexistant.
	 * @throws InvariantBroken problemen consistance client.
	 */
	public void modifierClient(final Client client, final String nom, 
			final String prenom, 
			final String adresse,
			final String catnom, final int code) 
					throws OperationImpossible, InvariantBroken {
		HashClient newHash;
		HashClient oldHash = 
				new HashClient(client.getNom(), client.getPrenom());
		boolean needNewHash = false;
		if (!lesClients.containsKey(oldHash)) {
			throw new OperationImpossible("Client " + nom + " " + prenom
					+ " inexistant");
		}
		if (!adresse.equals(client.getAdresse())) {
			client.setAddresse(adresse);
		}
		if (!nom.equals(client.getNom())) {
			client.setNom(nom);
			needNewHash = true;
		}
		if (!prenom.equals(client.getPrenom())) {
			client.setPrenom(prenom);
			needNewHash = true;
		}

		if (needNewHash) {
			newHash = new HashClient(nom, prenom);
			lesClients.remove(oldHash);
			lesClients.put(newHash, client);
		}
		CategorieClient catcli = chercherCatClient(catnom);
		if (!catcli.equals(client.getCategorie())) {
			if (catcli.getCodeReducUtilise()) {
				client.setCategorie(catcli, code);
			} else {
				client.setCategorie(catcli);
			}
		}
	}

	/**
	 * changerCategorie modifie la categorie du client.
	 * @param nom du client
	 * @param prenom du client
	 * @param catName nom de la nouvelle categorie
	 * @param reduc reduction si utilisee
	 * @throws OperationImpossible si client non trouvé,
	 * ou si categorie non trouvée.
	 * @throws InvariantBroken problemen consistance client.
	 */
	public void changerCategorie(final String nom, final String prenom, 
			final String catName, final int reduc)
					throws OperationImpossible, InvariantBroken {
		Client c = chercherClient(nom, prenom);
		if (c == null) {
			throw new OperationImpossible("Client " + nom + " " 
					+ prenom + " non trouve");
		}
		CategorieClient cat = chercherCatClient(catName);
		if (cat == null) {
			throw new OperationImpossible("Categorie client " 
					+ catName + " non trouvee");
		}
		if (cat.getCodeReducUtilise()) {
			c.setCategorie(cat, reduc);
		} else {
			c.setCategorie(cat);
		}
	}

	/**
	 * changerCodeReduction modifie le code de reduction d'un client.
	 * @param nom du client
	 * @param prenom du client
	 * @param reduc  code de réduction
	 * @throws OperationImpossible si client inexistant ou 
	 * si changement de code de reduction sur une categorie sans 
	 * code de reduction.
	 */
	public void changerCodeReduction(final String nom, final String prenom, 
			final int reduc)
					throws OperationImpossible {
		Client c = chercherClient(nom, prenom);
		if (c == null) {
			throw new OperationImpossible("Client " + nom + " " 
					+ prenom + " non trouve");
		}
		CategorieClient cat = c.getCategorie();
		if (cat.getCodeReducUtilise()) {
			c.setReduc(reduc);
		} else {
			throw new OperationImpossible("Changement de code de reduction "
					+ "sur une categorie sans code");
		}
	}

	/**
	 * <TT>chercherClient</TT> cherche le client dont les nom et
	 * prenom sont donnes en parametre.
	 *    @param nom Nom du client a chercher
	 *    @param prenom Prenom du client a chercher
	 *    @return Le client ou <code>null</code> en cas d'echec
	 */
	public Client chercherClient(final String nom, final String prenom) {
		HashClient hc = new HashClient(nom, prenom);
		if (lesClients.containsKey(hc)) {
			return lesClients.get(hc);
		}
		return null;
	}

	/**
	 * affiche les Clients en cours.
	 */
	public void listerClients() {
		System.out.println("Mediatheque " + nomMedia 
				+ "  listage des clients au "
				+ Datutil.dateToString(Datutil.dateDuJour()));
		if (lesClients.isEmpty()) {
			System.out.println("(neant)");
		} else {
			for (Client c : lesClients.values()) {
				System.out.println(c);
			}
		}
	}

	/**
	 * existeClient teste s'il existe un client de cette categorie
	 * pour eviter de detruire une categorie si un client la reference encore.
	 * @param cat la categorie du client
	 * @return true si elle existe false sinon
	 */
	public boolean existeClient(final CategorieClient cat) {
		for (Client c : lesClients.values()) {
			if (c.getCategorie().equals(cat)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * retourne le client correspondant au rang dans la collection.
	 * @param n rang du client dans la collection.
	 * @return client.
	 */
	public Client getClientAt(final int n) {
		int i;
		Collection<Client> colClient = lesClients.values();
		Iterator<Client> ic = colClient.iterator();
		Client cl = null;
		for (i = 0; i <= n; i++) {
			if (ic.hasNext()) {
				cl = ic.next();
			} else {
				break;
			}
		}
		return cl;
	}
	/**
	 * taille de la collection des clients.
	 * @return taille de la collection
	 */
	public int getClientsSize() {
		return lesClients.size();
	}
	/**
	 * cherche un client à partir de son nom. Fonction relai vers
	 * chercherClient.
	 * @param nom nom du client
	 * @param prenom du client
	 * @return client correspondant ou null.
	 */
	public Client findClient(final String nom, final String prenom) {
		return chercherClient(nom, prenom);
	}

	//Affichage du contenu des vecteurs
	/**
	 * <TT>afficherStatistiques</TT> affiche les statistiques
	 * globales des classes.
	 */
	public void afficherStatistiques() {
		if (debug) {
			System.out.println("Statistiques globales de la mediatheque \""
					+ nomMedia + "\" :");
		}
		FicheEmprunt.afficherStatistiques();
		System.out.println("Audio :" + Audio.getNbEmpruntsTotal());
		System.out.println("Video :" + Video.getNbEmpruntsTotal());
		System.out.println("Livre :" + Livre.getNbEmpruntsTotal());
		System.out.println("Client :" + Client.getNbEmpruntsTotal());
	}

	/** Accesseur de l'attribut nom.
	 *  @return nom de la mediatheque
	 */
	public String getNom() {
		return nomMedia;
	}
	/**
	 * initialisation des attributs à partir des objets
	 *  serialisés dans un fichier.
	 * @return true si deserialisation ok, false sinon.
	 */
	public boolean initFromFile() {
		FileInputStream fin;
		Mediatheque media = null;

		try {
			fin = new FileInputStream(nomMedia + ".data");
		} catch (FileNotFoundException fe) {
			System.out.println(fe);
			return false;
		}
		ObjectInputStream ois;

		try {
			ois = new ObjectInputStream(fin);
			media = (Mediatheque) ois.readObject();
			lesCatsClient = media.lesCatsClient;
			lesGenres = media.lesGenres;
			lesLocalisations = media.lesLocalisations;
			lesClients = media.lesClients;
			lesDocuments = media.lesDocuments;
			lesEmprunts = media.lesEmprunts;

			ois.close();
			fin.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
			System.out.println("Error reading mediatheque data");
		} catch (ClassNotFoundException cnfe) {
			System.out.println(cnfe);
			System.out.println("Error finding mediatheque class");
		}
		return true;
	}
	/**
	 * serialisation des attributs dans un fichier du nom de 
	 * la mediatheque.data.
	 * @return true si ok, false sinon.
	 */
	public boolean saveToFile() {
		FileOutputStream fout;

		try {
			fout = new FileOutputStream(nomMedia + ".data");
		} catch (FileNotFoundException fe) {
			System.out.println(fe);
			return false;
		}
		ObjectOutputStream oos;

		try {
			oos = new ObjectOutputStream(fout);
			oos.writeObject(this);
			oos.close();
			fout.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
			System.out.println("Error writing mediatheque data");
			return false;
		}
		return true;
	}
}
