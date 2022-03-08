package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Aeropuerto;
import com.mycompany.myapp.domain.Avion;
import com.mycompany.myapp.domain.Piloto;
import com.mycompany.myapp.domain.Tripulante;
import com.mycompany.myapp.domain.Vuelo;
import com.mycompany.myapp.repository.VueloRepository;
import com.mycompany.myapp.service.VueloService;
import com.mycompany.myapp.service.criteria.VueloCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VueloResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VueloResourceIT {

    private static final Boolean DEFAULT_PASAPORTE_COVID = false;
    private static final Boolean UPDATED_PASAPORTE_COVID = true;

    private static final String DEFAULT_NUMERO_DE_VUELO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_DE_VUELO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/vuelos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VueloRepository vueloRepository;

    @Mock
    private VueloRepository vueloRepositoryMock;

    @Mock
    private VueloService vueloServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVueloMockMvc;

    private Vuelo vuelo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vuelo createEntity(EntityManager em) {
        Vuelo vuelo = new Vuelo().pasaporteCovid(DEFAULT_PASAPORTE_COVID).numeroDeVuelo(DEFAULT_NUMERO_DE_VUELO);
        return vuelo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vuelo createUpdatedEntity(EntityManager em) {
        Vuelo vuelo = new Vuelo().pasaporteCovid(UPDATED_PASAPORTE_COVID).numeroDeVuelo(UPDATED_NUMERO_DE_VUELO);
        return vuelo;
    }

    @BeforeEach
    public void initTest() {
        vuelo = createEntity(em);
    }

    @Test
    @Transactional
    void createVuelo() throws Exception {
        int databaseSizeBeforeCreate = vueloRepository.findAll().size();
        // Create the Vuelo
        restVueloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vuelo)))
            .andExpect(status().isCreated());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeCreate + 1);
        Vuelo testVuelo = vueloList.get(vueloList.size() - 1);
        assertThat(testVuelo.getPasaporteCovid()).isEqualTo(DEFAULT_PASAPORTE_COVID);
        assertThat(testVuelo.getNumeroDeVuelo()).isEqualTo(DEFAULT_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void createVueloWithExistingId() throws Exception {
        // Create the Vuelo with an existing ID
        vuelo.setId(1L);

        int databaseSizeBeforeCreate = vueloRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVueloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vuelo)))
            .andExpect(status().isBadRequest());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPasaporteCovidIsRequired() throws Exception {
        int databaseSizeBeforeTest = vueloRepository.findAll().size();
        // set the field null
        vuelo.setPasaporteCovid(null);

        // Create the Vuelo, which fails.

        restVueloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vuelo)))
            .andExpect(status().isBadRequest());

        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumeroDeVueloIsRequired() throws Exception {
        int databaseSizeBeforeTest = vueloRepository.findAll().size();
        // set the field null
        vuelo.setNumeroDeVuelo(null);

        // Create the Vuelo, which fails.

        restVueloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vuelo)))
            .andExpect(status().isBadRequest());

        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVuelos() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList
        restVueloMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vuelo.getId().intValue())))
            .andExpect(jsonPath("$.[*].pasaporteCovid").value(hasItem(DEFAULT_PASAPORTE_COVID.booleanValue())))
            .andExpect(jsonPath("$.[*].numeroDeVuelo").value(hasItem(DEFAULT_NUMERO_DE_VUELO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVuelosWithEagerRelationshipsIsEnabled() throws Exception {
        when(vueloServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVueloMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(vueloServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVuelosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(vueloServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVueloMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(vueloServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getVuelo() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get the vuelo
        restVueloMockMvc
            .perform(get(ENTITY_API_URL_ID, vuelo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vuelo.getId().intValue()))
            .andExpect(jsonPath("$.pasaporteCovid").value(DEFAULT_PASAPORTE_COVID.booleanValue()))
            .andExpect(jsonPath("$.numeroDeVuelo").value(DEFAULT_NUMERO_DE_VUELO));
    }

    @Test
    @Transactional
    void getVuelosByIdFiltering() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        Long id = vuelo.getId();

        defaultVueloShouldBeFound("id.equals=" + id);
        defaultVueloShouldNotBeFound("id.notEquals=" + id);

        defaultVueloShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVueloShouldNotBeFound("id.greaterThan=" + id);

        defaultVueloShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVueloShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVuelosByPasaporteCovidIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where pasaporteCovid equals to DEFAULT_PASAPORTE_COVID
        defaultVueloShouldBeFound("pasaporteCovid.equals=" + DEFAULT_PASAPORTE_COVID);

        // Get all the vueloList where pasaporteCovid equals to UPDATED_PASAPORTE_COVID
        defaultVueloShouldNotBeFound("pasaporteCovid.equals=" + UPDATED_PASAPORTE_COVID);
    }

    @Test
    @Transactional
    void getAllVuelosByPasaporteCovidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where pasaporteCovid not equals to DEFAULT_PASAPORTE_COVID
        defaultVueloShouldNotBeFound("pasaporteCovid.notEquals=" + DEFAULT_PASAPORTE_COVID);

        // Get all the vueloList where pasaporteCovid not equals to UPDATED_PASAPORTE_COVID
        defaultVueloShouldBeFound("pasaporteCovid.notEquals=" + UPDATED_PASAPORTE_COVID);
    }

    @Test
    @Transactional
    void getAllVuelosByPasaporteCovidIsInShouldWork() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where pasaporteCovid in DEFAULT_PASAPORTE_COVID or UPDATED_PASAPORTE_COVID
        defaultVueloShouldBeFound("pasaporteCovid.in=" + DEFAULT_PASAPORTE_COVID + "," + UPDATED_PASAPORTE_COVID);

        // Get all the vueloList where pasaporteCovid equals to UPDATED_PASAPORTE_COVID
        defaultVueloShouldNotBeFound("pasaporteCovid.in=" + UPDATED_PASAPORTE_COVID);
    }

    @Test
    @Transactional
    void getAllVuelosByPasaporteCovidIsNullOrNotNull() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where pasaporteCovid is not null
        defaultVueloShouldBeFound("pasaporteCovid.specified=true");

        // Get all the vueloList where pasaporteCovid is null
        defaultVueloShouldNotBeFound("pasaporteCovid.specified=false");
    }

    @Test
    @Transactional
    void getAllVuelosByNumeroDeVueloIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where numeroDeVuelo equals to DEFAULT_NUMERO_DE_VUELO
        defaultVueloShouldBeFound("numeroDeVuelo.equals=" + DEFAULT_NUMERO_DE_VUELO);

        // Get all the vueloList where numeroDeVuelo equals to UPDATED_NUMERO_DE_VUELO
        defaultVueloShouldNotBeFound("numeroDeVuelo.equals=" + UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void getAllVuelosByNumeroDeVueloIsNotEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where numeroDeVuelo not equals to DEFAULT_NUMERO_DE_VUELO
        defaultVueloShouldNotBeFound("numeroDeVuelo.notEquals=" + DEFAULT_NUMERO_DE_VUELO);

        // Get all the vueloList where numeroDeVuelo not equals to UPDATED_NUMERO_DE_VUELO
        defaultVueloShouldBeFound("numeroDeVuelo.notEquals=" + UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void getAllVuelosByNumeroDeVueloIsInShouldWork() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where numeroDeVuelo in DEFAULT_NUMERO_DE_VUELO or UPDATED_NUMERO_DE_VUELO
        defaultVueloShouldBeFound("numeroDeVuelo.in=" + DEFAULT_NUMERO_DE_VUELO + "," + UPDATED_NUMERO_DE_VUELO);

        // Get all the vueloList where numeroDeVuelo equals to UPDATED_NUMERO_DE_VUELO
        defaultVueloShouldNotBeFound("numeroDeVuelo.in=" + UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void getAllVuelosByNumeroDeVueloIsNullOrNotNull() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where numeroDeVuelo is not null
        defaultVueloShouldBeFound("numeroDeVuelo.specified=true");

        // Get all the vueloList where numeroDeVuelo is null
        defaultVueloShouldNotBeFound("numeroDeVuelo.specified=false");
    }

    @Test
    @Transactional
    void getAllVuelosByNumeroDeVueloContainsSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where numeroDeVuelo contains DEFAULT_NUMERO_DE_VUELO
        defaultVueloShouldBeFound("numeroDeVuelo.contains=" + DEFAULT_NUMERO_DE_VUELO);

        // Get all the vueloList where numeroDeVuelo contains UPDATED_NUMERO_DE_VUELO
        defaultVueloShouldNotBeFound("numeroDeVuelo.contains=" + UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void getAllVuelosByNumeroDeVueloNotContainsSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        // Get all the vueloList where numeroDeVuelo does not contain DEFAULT_NUMERO_DE_VUELO
        defaultVueloShouldNotBeFound("numeroDeVuelo.doesNotContain=" + DEFAULT_NUMERO_DE_VUELO);

        // Get all the vueloList where numeroDeVuelo does not contain UPDATED_NUMERO_DE_VUELO
        defaultVueloShouldBeFound("numeroDeVuelo.doesNotContain=" + UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void getAllVuelosByAeropuertoIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);
        Aeropuerto aeropuerto;
        if (TestUtil.findAll(em, Aeropuerto.class).isEmpty()) {
            aeropuerto = AeropuertoResourceIT.createEntity(em);
            em.persist(aeropuerto);
            em.flush();
        } else {
            aeropuerto = TestUtil.findAll(em, Aeropuerto.class).get(0);
        }
        em.persist(aeropuerto);
        em.flush();
        vuelo.setAeropuerto(aeropuerto);
        vueloRepository.saveAndFlush(vuelo);
        Long aeropuertoId = aeropuerto.getId();

        // Get all the vueloList where aeropuerto equals to aeropuertoId
        defaultVueloShouldBeFound("aeropuertoId.equals=" + aeropuertoId);

        // Get all the vueloList where aeropuerto equals to (aeropuertoId + 1)
        defaultVueloShouldNotBeFound("aeropuertoId.equals=" + (aeropuertoId + 1));
    }

    @Test
    @Transactional
    void getAllVuelosByDestinoAeropuertoIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);
        Aeropuerto destinoAeropuerto;
        if (TestUtil.findAll(em, Aeropuerto.class).isEmpty()) {
            destinoAeropuerto = AeropuertoResourceIT.createEntity(em);
            em.persist(destinoAeropuerto);
            em.flush();
        } else {
            destinoAeropuerto = TestUtil.findAll(em, Aeropuerto.class).get(0);
        }
        em.persist(destinoAeropuerto);
        em.flush();
        vuelo.setDestinoAeropuerto(destinoAeropuerto);
        vueloRepository.saveAndFlush(vuelo);
        Long destinoAeropuertoId = destinoAeropuerto.getId();

        // Get all the vueloList where destinoAeropuerto equals to destinoAeropuertoId
        defaultVueloShouldBeFound("destinoAeropuertoId.equals=" + destinoAeropuertoId);

        // Get all the vueloList where destinoAeropuerto equals to (destinoAeropuertoId + 1)
        defaultVueloShouldNotBeFound("destinoAeropuertoId.equals=" + (destinoAeropuertoId + 1));
    }

    @Test
    @Transactional
    void getAllVuelosByAvionIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);
        Avion avion;
        if (TestUtil.findAll(em, Avion.class).isEmpty()) {
            avion = AvionResourceIT.createEntity(em);
            em.persist(avion);
            em.flush();
        } else {
            avion = TestUtil.findAll(em, Avion.class).get(0);
        }
        em.persist(avion);
        em.flush();
        vuelo.setAvion(avion);
        vueloRepository.saveAndFlush(vuelo);
        Long avionId = avion.getId();

        // Get all the vueloList where avion equals to avionId
        defaultVueloShouldBeFound("avionId.equals=" + avionId);

        // Get all the vueloList where avion equals to (avionId + 1)
        defaultVueloShouldNotBeFound("avionId.equals=" + (avionId + 1));
    }

    @Test
    @Transactional
    void getAllVuelosByPilotoIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);
        Piloto piloto;
        if (TestUtil.findAll(em, Piloto.class).isEmpty()) {
            piloto = PilotoResourceIT.createEntity(em);
            em.persist(piloto);
            em.flush();
        } else {
            piloto = TestUtil.findAll(em, Piloto.class).get(0);
        }
        em.persist(piloto);
        em.flush();
        vuelo.setPiloto(piloto);
        vueloRepository.saveAndFlush(vuelo);
        Long pilotoId = piloto.getId();

        // Get all the vueloList where piloto equals to pilotoId
        defaultVueloShouldBeFound("pilotoId.equals=" + pilotoId);

        // Get all the vueloList where piloto equals to (pilotoId + 1)
        defaultVueloShouldNotBeFound("pilotoId.equals=" + (pilotoId + 1));
    }

    @Test
    @Transactional
    void getAllVuelosByTripulanteIsEqualToSomething() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);
        Tripulante tripulante;
        if (TestUtil.findAll(em, Tripulante.class).isEmpty()) {
            tripulante = TripulanteResourceIT.createEntity(em);
            em.persist(tripulante);
            em.flush();
        } else {
            tripulante = TestUtil.findAll(em, Tripulante.class).get(0);
        }
        em.persist(tripulante);
        em.flush();
        vuelo.addTripulante(tripulante);
        vueloRepository.saveAndFlush(vuelo);
        Long tripulanteId = tripulante.getId();

        // Get all the vueloList where tripulante equals to tripulanteId
        defaultVueloShouldBeFound("tripulanteId.equals=" + tripulanteId);

        // Get all the vueloList where tripulante equals to (tripulanteId + 1)
        defaultVueloShouldNotBeFound("tripulanteId.equals=" + (tripulanteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVueloShouldBeFound(String filter) throws Exception {
        restVueloMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vuelo.getId().intValue())))
            .andExpect(jsonPath("$.[*].pasaporteCovid").value(hasItem(DEFAULT_PASAPORTE_COVID.booleanValue())))
            .andExpect(jsonPath("$.[*].numeroDeVuelo").value(hasItem(DEFAULT_NUMERO_DE_VUELO)));

        // Check, that the count call also returns 1
        restVueloMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVueloShouldNotBeFound(String filter) throws Exception {
        restVueloMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVueloMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVuelo() throws Exception {
        // Get the vuelo
        restVueloMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVuelo() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();

        // Update the vuelo
        Vuelo updatedVuelo = vueloRepository.findById(vuelo.getId()).get();
        // Disconnect from session so that the updates on updatedVuelo are not directly saved in db
        em.detach(updatedVuelo);
        updatedVuelo.pasaporteCovid(UPDATED_PASAPORTE_COVID).numeroDeVuelo(UPDATED_NUMERO_DE_VUELO);

        restVueloMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVuelo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVuelo))
            )
            .andExpect(status().isOk());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
        Vuelo testVuelo = vueloList.get(vueloList.size() - 1);
        assertThat(testVuelo.getPasaporteCovid()).isEqualTo(UPDATED_PASAPORTE_COVID);
        assertThat(testVuelo.getNumeroDeVuelo()).isEqualTo(UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void putNonExistingVuelo() throws Exception {
        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();
        vuelo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVueloMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vuelo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(vuelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVuelo() throws Exception {
        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();
        vuelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVueloMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(vuelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVuelo() throws Exception {
        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();
        vuelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVueloMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vuelo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVueloWithPatch() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();

        // Update the vuelo using partial update
        Vuelo partialUpdatedVuelo = new Vuelo();
        partialUpdatedVuelo.setId(vuelo.getId());

        partialUpdatedVuelo.pasaporteCovid(UPDATED_PASAPORTE_COVID);

        restVueloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVuelo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVuelo))
            )
            .andExpect(status().isOk());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
        Vuelo testVuelo = vueloList.get(vueloList.size() - 1);
        assertThat(testVuelo.getPasaporteCovid()).isEqualTo(UPDATED_PASAPORTE_COVID);
        assertThat(testVuelo.getNumeroDeVuelo()).isEqualTo(DEFAULT_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void fullUpdateVueloWithPatch() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();

        // Update the vuelo using partial update
        Vuelo partialUpdatedVuelo = new Vuelo();
        partialUpdatedVuelo.setId(vuelo.getId());

        partialUpdatedVuelo.pasaporteCovid(UPDATED_PASAPORTE_COVID).numeroDeVuelo(UPDATED_NUMERO_DE_VUELO);

        restVueloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVuelo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVuelo))
            )
            .andExpect(status().isOk());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
        Vuelo testVuelo = vueloList.get(vueloList.size() - 1);
        assertThat(testVuelo.getPasaporteCovid()).isEqualTo(UPDATED_PASAPORTE_COVID);
        assertThat(testVuelo.getNumeroDeVuelo()).isEqualTo(UPDATED_NUMERO_DE_VUELO);
    }

    @Test
    @Transactional
    void patchNonExistingVuelo() throws Exception {
        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();
        vuelo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVueloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vuelo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(vuelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVuelo() throws Exception {
        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();
        vuelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVueloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(vuelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVuelo() throws Exception {
        int databaseSizeBeforeUpdate = vueloRepository.findAll().size();
        vuelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVueloMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(vuelo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vuelo in the database
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVuelo() throws Exception {
        // Initialize the database
        vueloRepository.saveAndFlush(vuelo);

        int databaseSizeBeforeDelete = vueloRepository.findAll().size();

        // Delete the vuelo
        restVueloMockMvc
            .perform(delete(ENTITY_API_URL_ID, vuelo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Vuelo> vueloList = vueloRepository.findAll();
        assertThat(vueloList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
