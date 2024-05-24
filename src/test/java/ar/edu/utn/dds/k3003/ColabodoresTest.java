package ar.edu.utn.k3003;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.CoeficientesPuntos;
import ar.edu.utn.dds.k3003.model.TipoCoeficiente;
import ar.edu.utn.dds.k3003.tests.TestTP;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class ColabodoresTest {
  final String nombre1 = "bel";
  ColaboradorDTO colaborador1;
  Fachada instancia;
  @Mock

  private FachadaViandas mockViandas;
  @Mock

  private FachadaLogistica mockLogistica;
  @Mock
  private CoeficientesPuntos coeficientesPuntos;

  public ColabodoresTest() {
  }
  @BeforeEach
  public void setUp() {
    this.instancia = new Fachada();
    Objects.requireNonNull(this);
    this.colaborador1 = new ColaboradorDTO("bel", List.of(FormaDeColaborarEnum.DONADOR));
    instancia.setViandasProxy(mockViandas);
    instancia.setLogisticaProxy(mockLogistica);
    this.coeficientesPuntos = new CoeficientesPuntos(0.5,1,1.5,2,5);
  }

  @Test
  @DisplayName("Agregar colaborador")
  void agregarColaborador() {
    ColaboradorDTO colaboradorRta = this.instancia.agregar(this.colaborador1);
    ColaboradorDTO colaborador2 = this.instancia.buscarXId(colaboradorRta.getId());
    Objects.requireNonNull(this);
    Assertions.assertEquals("bel", colaborador2.getNombre(), "No se esta recuperando el nombre del colaborador correctamente.");
  }
  @Test
  @DisplayName("Verificar que asigna ID a colaborador")
  void verificarIdColaborador() {
    ColaboradorDTO colaboradorRta = this.instancia.agregar(this.colaborador1);
    Assertions.assertNotNull(colaboradorRta.getId(), "Colaboradores#agregar debe retornar un ColaboradorDTO con un id inicializado.");
  }
  @Test
  @DisplayName("Modificar formas de colaborar")
  void modificarFormasColaborar() {
    ColaboradorDTO colaboradorRta = this.instancia.agregar(this.colaborador1);
    ColaboradorDTO colaboradorRta2 = this.instancia.modificar(colaboradorRta.getId(), List.of(FormaDeColaborarEnum.TRANSPORTADOR));
    Assertions.assertEquals(FormaDeColaborarEnum.TRANSPORTADOR, colaboradorRta2.getFormas().get(0), "No se actualizó la forma de colaborar.");
    ColaboradorDTO colaborador3 = this.instancia.buscarXId(colaboradorRta.getId());
    Assertions.assertEquals(FormaDeColaborarEnum.TRANSPORTADOR, colaborador3.getFormas().get(0), "No se esta guardando la forma de colaborar.");
  }
  @Test
  @DisplayName("Calcular puntos de colaborador con dos entregas y dos traslados. Coef. viandasDistribuidas 1 y Coef. viandasDonadas 1.5")
  void puntosColaboradorConViandasEntregadasYTraslados() {
    ColaboradorDTO colaboradorRta = this.instancia.agregar(this.colaborador1);

    this.instancia.actualizarPesosPuntos(0.5,1.0,1.5,2.0,5.0);

    TrasladoDTO trasladoDTO1 = new TrasladoDTO("x", 18, 19);
    TrasladoDTO trasladoDTO2 = new TrasladoDTO("x", 18, 19);
    ViandaDTO viandaDTO1 = new ViandaDTO("traslado1", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, colaboradorRta.getId(), 20);
    ViandaDTO viandaDTO2 = new ViandaDTO("traslado2", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, colaboradorRta.getId(), 20);

    List<ViandaDTO> viandasDTO = new ArrayList<>();
    viandasDTO.add(viandaDTO1);
    viandasDTO.add(viandaDTO2);
    List<TrasladoDTO> trasladosDTO = new ArrayList<>();
    trasladosDTO.add(trasladoDTO1);
    trasladosDTO.add(trasladoDTO2);

    when(this.mockViandas.viandasDeColaborador(colaboradorRta.getId(), 1, 2024)).thenReturn(viandasDTO);
    when(this.mockLogistica.trasladosDeColaborador(colaboradorRta.getId(), 1, 2024)).thenReturn(trasladosDTO);

    Double puntos = this.instancia.puntos(colaboradorRta.getId());

    Assertions.assertEquals(5.0, puntos, "El cálculo de puntos no es el esperado");
  }


  @Test
  @DisplayName("Calcular puntos de colaborador con dos entregas y dos traslados. Coef. viandasDistribuidas 2 y Coef. viandasDonadas 2.5")
  void puntosColaboradorConViandasEntregadasYTraslados2() {
    ColaboradorDTO colaboradorRta = this.instancia.agregar(this.colaborador1);

    this.instancia.actualizarPesosPuntos(0.5,2.0,2.5,2.0,5.0);

    TrasladoDTO trasladoDTO1 = new TrasladoDTO("x", 18, 19);
    TrasladoDTO trasladoDTO2 = new TrasladoDTO("x", 18, 19);
    ViandaDTO viandaDTO1 = new ViandaDTO("traslado1", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, colaboradorRta.getId(), 20);
    ViandaDTO viandaDTO2 = new ViandaDTO("traslado2", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, colaboradorRta.getId(), 20);

    List<ViandaDTO> viandasDTO = new ArrayList<>();
    viandasDTO.add(viandaDTO1);
    viandasDTO.add(viandaDTO2);
    List<TrasladoDTO> trasladosDTO = new ArrayList<>();
    trasladosDTO.add(trasladoDTO1);
    trasladosDTO.add(trasladoDTO2);

    when(this.mockViandas.viandasDeColaborador(colaboradorRta.getId(), 1, 2024)).thenReturn(viandasDTO);
    when(this.mockLogistica.trasladosDeColaborador(colaboradorRta.getId(), 1, 2024)).thenReturn(trasladosDTO);

    Double puntos = this.instancia.puntos(colaboradorRta.getId());

    Assertions.assertEquals(9.0, puntos, "El cálculo de puntos no es el esperado");
  }

  @Test
  @DisplayName("Calcular puntos sin entregas ni traslados")
  void puntosColaboradorSinEntregasNiTraslados() {
    ColaboradorDTO colaboradorRta = this.instancia.agregar(this.colaborador1);

    this.instancia.actualizarPesosPuntos(0.5,1.0,1.5,2.0,5.0);

    List<ViandaDTO> viandasDTO = new ArrayList<>();
    List<TrasladoDTO> trasladosDTO = new ArrayList<>();

    when(this.mockViandas.viandasDeColaborador(colaboradorRta.getId(), 1, 2024)).thenReturn(viandasDTO);
    when(this.mockLogistica.trasladosDeColaborador(colaboradorRta.getId(), 1, 2024)).thenReturn(trasladosDTO);

    Double puntos = this.instancia.puntos(colaboradorRta.getId());

    Assertions.assertEquals(0.0, puntos, "El cálculo de puntos no es el esperado");
  }

}
