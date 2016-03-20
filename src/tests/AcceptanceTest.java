package tests;

import mediatheque.Genre;
import mediatheque.Localisation;
import mediatheque.Mediatheque;
import mediatheque.OperationImpossible;
import mediatheque.client.Client;
import mediatheque.document.Document;
import mediatheque.document.Video;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import util.Datutil;
import util.InvariantBroken;

/**
 * Acceptance Test Document
 * 
 * @author J Paul Gibson
 * @author C Bac
 */

public class AcceptanceTest {

	private Mediatheque m1;

	@Before
	public void setUp() throws OperationImpossible, InvariantBroken {
		m1 = new Mediatheque("MediathequeTest");
		m1.ajouterGenre("Test_genre1");
		m1.ajouterGenre("Test_genre2");
		Genre g = m1.chercherGenre("Test_genre1");
		Localisation l ;
		m1.ajouterLocalisation("Test_salle1", "Test_rayon1");
		l = m1.chercherLocalisation("Test_salle1", "Test_rayon1");
		Document d1 = new Video("Test_code1", l, "Test_titre1", "Test_auteur1",
				"Test_annee1", g, 120, "Test_mentionLegale1");
		m1.ajouterDocument(d1);
		m1.ajouterDocument(new Video("Test_code2", l, "Test_titre2",
				"Test_auteur2", "Test_annee2", g, 120, "Test_mentionLegale1"));
		m1.ajouterDocument(new Video("Test_code3", l, "Test_titre3",
				"Test_auteur3", "Test_annee3", g, 120, "Test_mentionLegale1"));
		m1.ajouterDocument(new Video("Non_empruntable", l, "Test_titre3",
				"Test_auteur3", "Test_annee3", g, 120, "Test_mentionLegale1"));
		m1.metEmpruntable("Test_code1");
		m1.metEmpruntable("Test_code2");
		m1.metEmpruntable("Test_code3");

		m1.ajouterCatClient("TarifNormal", 2, 25, 1.0, 1.0, false);
		m1.inscrire("nom1", "prenom1", "adresse1", "TarifNormal");
		m1.inscrire("nom2", "prenom2", "adresse2", "TarifNormal");
	}

	@After
	public void tearDown() {
		m1 = null;
	}
	@Test
	public void setupOK() throws OperationImpossible, InvariantBroken {
		Client c = m1.chercherClient("nom1", "prenom1");
		Assert.assertNotNull(c);
		Assert.assertEquals(0, c.getNbEmpruntsEnCours());
		Assert.assertTrue(c.peutEmprunter());
		
		Document d = m1.chercherDocument("Test_code1");
		Assert.assertNotNull(d) ;
		Assert.assertTrue(d.estEmpruntable());		
		Assert.assertEquals(0, d.getNbEmprunts());
		d = m1.chercherDocument("Test_code2");
		Assert.assertNotNull(d) ;
		Assert.assertTrue(d.estEmpruntable());		
		Assert.assertEquals(0, d.getNbEmprunts());
		d = m1.chercherDocument("Test_code3");
		Assert.assertNotNull(d) ;
		Assert.assertTrue(d.estEmpruntable());		
		Assert.assertEquals(0, d.getNbEmprunts());
		Document d1 = m1.chercherDocument("Non_empruntable");
		Assert.assertNotNull(d1);
		Assert.assertEquals(0,d1.getNbEmprunts());
		Assert.assertFalse(d1.estEmpruntable());
	}
	/**
	 * Essai d'emprunter un document par un client non inscrit doit lever
	 * l'exception OperationImpossible
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test(expected = OperationImpossible.class)
	public void emprunterClientNonInscrit() throws OperationImpossible,
			InvariantBroken {
		m1.emprunter("nom", "prenom", "Test_code1");
		Assert.fail("Emprunter avec un client non inscrit doit lever l'exception OperationImpossible");
	}

	/**
	 * Emprunt d'un document par un client puis non retour dans les 14 jours le
	 * client doit etre marque en retard et donc ne doit pas pouvoir emprunter
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test
	public void emprunterEnRetard() throws OperationImpossible, InvariantBroken {
		Client c1 = m1.chercherClient("nom1", "prenom1");
		m1.emprunter("nom1", "prenom1", "Test_code1");
		Datutil.addAuJour(1);
		m1.verifier();
		Assert.assertTrue(c1.peutEmprunter());
		Datutil.addAuJour(4*7);
		m1.verifier();
		Assert.assertFalse(c1.peutEmprunter());
	}
	
	/**
	 * Test number 3, tries to borrow too many documents
	 * Should raise an OperationImpossible
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test(expected = OperationImpossible.class)
	public void emprunterTropDeDocuments()
	throws OperationImpossible, InvariantBroken {
		Client c1 = m1.chercherClient("nom1", "prenom1");
		Assert.assertTrue(c1.peutEmprunter());
		
		//The client can only borrows 2 documents
		m1.emprunter("nom1", "prenom1", "Test_code1");
		m1.emprunter("nom1", "prenom1", "Test_code2");
		
		//Tries to borrow a third one
		m1.emprunter("nom1", "prenom1", "Test_code3");
		Assert.fail("Emprunter trop de documents doit lever l'exception OperationImpossible");
	}

	/**
	 * Test number 4, tries to borrow a document that doesn't exist
	 * Should raise an OperationImpossible
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test(expected = OperationImpossible.class)
	public void emprunterDocumentInexistant()
	throws OperationImpossible, InvariantBroken {
		m1.emprunter("nom1", "prenom1", "Test98");
		Assert.fail("Emprunter un document inexistant doit lever l'exception OperationImpossible");
	}

	/**
	 * Test number 5, tries to borrow an unborrowable document
	 * Should raise an OperationImpossible
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test(expected = OperationImpossible.class)
	public void emprunterDocumentNonEmpruntable()
	throws OperationImpossible, InvariantBroken {
		m1.emprunter("nom1", "prenom1", "Nom_empruntable");
		Assert.fail("Emprunter un document non empruntable doit lever l'exception OperationImpossible");
	}
	
	/**
	 * Test number 6, tries to borrow an unavailable document
	 * Should raise an OperationImpossible
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test(expected = OperationImpossible.class)
	public void emprunterDocumentNonDisponible()
	throws OperationImpossible, InvariantBroken {
		m1.emprunter("nom1", "prenom1", "Test_code1");
		m1.emprunter("nom2", "prenom2", "Test_code1");
		Assert.fail("Emprunter un document indisponible doit lever l'exception OperationImpossible");
	}
	
	/**
	 * Test number 7, successfully borrows a document
	 * 
	 * @throws OperationImpossible
	 * @throws InvariantBroken
	 */
	@Test
	public void empruntAccepte()
	throws OperationImpossible, InvariantBroken {
		Client c1 = m1.chercherClient("nom1", "prenom1");
		Assert.assertTrue(c1.peutEmprunter());
		m1.emprunter("nom1", "prenom1", "Test_code1");
	}
}

