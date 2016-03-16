package tests;

import mediatheque.Genre;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * La classe <code>JUnit_Genre</code> cree une instance de la classe Genre et
 * invoque des methodes sur cette instance.
 * 
 * @author Denis Conan
 */
public class JUnit_GenreTest {
	Genre g;

	/**
	 * Methode executee avant chaque test.
	 * 
	 * @throws Exception
	 *             l'exception levee par le test et attrapee par JUnit.
	 */
	@Before
	public void setUp() throws Exception {
		g = new Genre("Roman");
	}

	/**
	 * Methode executee apres chaque test.
	 * 
	 * @throws Exception
	 *             Exception l'exception levee par le test et attrapee par
	 *             JUnit.
	 */
	@After
	public void tearDown() throws Exception {
		g = null;
	}

	/**
	 * <tt>testCtr()</tt> reunit les tests pour verifier que le constructeur est
	 * OK.
	 */
	@Test
	public void testCtr() {
		Assert.assertEquals(g.getNbEmprunts(), 0);
		Assert.assertEquals(g.getNom(), "Roman");
	}

	/**
	 * <tt>testEmprunts()</tt> reunit les tests pour verifier que l'operation
	 * emprunter modifie bien le nombre d'emprunts.
	 */
	@Test
	public void testEmprunts() {
		// Effectuer 10 emprunts
		for (int i = 0; i < 10; i++) {
			g.emprunter();
		}
		Assert.assertEquals(g.getNbEmprunts(), 10);
	}

	/**
	 * <tt>testToString()</tt> verifie que l'operation toString retourne une
	 * chaine de caracteres attendue.
	 */
	@Test
	public void testToString() {
		Assert.assertEquals("Genre: Roman, nbemprunts:0", g.toString());
	}
}
