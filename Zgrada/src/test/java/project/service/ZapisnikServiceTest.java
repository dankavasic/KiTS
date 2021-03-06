package project.service;

import static project.constants.ZapisnikConstants.DB_DAT_KREIRANJA;
import static project.constants.ZapisnikConstants.DB_COUNT;
import static project.constants.ZapisnikConstants.DB_ID;
import static project.constants.ZapisnikConstants.DB_OPIS;
import static project.constants.ZapisnikConstants.DB_KREATOR_ID;
import static project.constants.ZapisnikConstants.DB_ZGRADA_ID;
import static project.constants.ZapisnikConstants.NEW_DAT_KREIRANJA;
import static project.constants.ZapisnikConstants.NEW_OPIS;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import project.MyApplication;
import project.model.Zapisnik;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MyApplication.class)
@WebIntegrationTest
@TestPropertySource(locations="classpath:test.properties")

public class ZapisnikServiceTest{
	@Autowired
	ZapisnikService zapisnikService;

	@Test
	public void testFindAll() {
		List<Zapisnik> zapisnici = zapisnikService.findAll();
		assertThat(zapisnici).hasSize(DB_COUNT);
	}
	@Test 
	public void testFindOne() {
		Zapisnik dbZapisnik= zapisnikService.findOne(DB_ID);
		assertThat(dbZapisnik).isNotNull();
		
		assertThat(dbZapisnik.getId()).isEqualTo(DB_ID);
		assertThat(dbZapisnik.getDatKreiranja()).hasSameTimeAs(DB_DAT_KREIRANJA);
		assertThat(dbZapisnik.getOpis()).isEqualTo(DB_OPIS);
		assertThat(dbZapisnik.getKreator().getId()).isEqualTo(DB_KREATOR_ID);
		assertThat(dbZapisnik.getZgrada().getId()).isEqualTo(DB_ZGRADA_ID);
	}

	@Test
    @Transactional
    @Rollback(true)
	public void testAdd() {
		Zapisnik zapisnik = new Zapisnik();
		zapisnik.setDatKreiranja(NEW_DAT_KREIRANJA);
		zapisnik.setOpis(NEW_OPIS);
		
		int dbSizeBeforeAdd = zapisnikService.findAll().size();
		
		Zapisnik dbZapisnik = zapisnikService.save(zapisnik);
		assertThat(dbZapisnik).isNotNull();
				
        List<Zapisnik> zapisnici = zapisnikService.findAll();
        assertThat(zapisnici).hasSize(dbSizeBeforeAdd + 1);
        dbZapisnik = zapisnici.get(zapisnici.size() - 1); 
        assertThat(dbZapisnik.getDatKreiranja()).isEqualTo(NEW_DAT_KREIRANJA);
        assertThat(dbZapisnik.getOpis()).isEqualTo(NEW_OPIS);
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	@Transactional
	@Rollback(true)
	public void testRemove() {
		int dbSizeBeforeRemove = zapisnikService.findAll().size();
		zapisnikService.delete(DB_ID);
		
		List<Zapisnik> zapisnici = zapisnikService.findAll();
		assertThat(zapisnici).hasSize(dbSizeBeforeRemove - 1);
		
		Zapisnik dbZapisnik = zapisnikService.findOne(DB_ID);
		assertThat(dbZapisnik).isNull();
	}

	@Test
	public void testFindByZgrada() {
		List<Zapisnik> zapisnici = zapisnikService.findByZgradaId(DB_ZGRADA_ID);
		assertThat(zapisnici).hasSize(DB_COUNT);
	}
	@Test
	public void testFindByKreator() {
		List<Zapisnik> zapisnici = zapisnikService.findByKreatorId(DB_KREATOR_ID);
		assertThat(zapisnici).hasSize(DB_COUNT);
	}

}