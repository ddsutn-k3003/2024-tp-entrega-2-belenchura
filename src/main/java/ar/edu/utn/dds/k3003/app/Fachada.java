package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.CoeficientesPuntos;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.TipoCoeficiente;
import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositories.ColaboradorRepository;
import ar.edu.utn.dds.k3003.Evaluador;

import java.util.List;
import java.util.NoSuchElementException;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaColaboradores {
  private ColaboradorRepository colaboradorRepository = new ColaboradorRepository();
  private ColaboradorMapper colaboradorMapper = new ColaboradorMapper();
  private CoeficientesPuntos coeficientesPuntos = new CoeficientesPuntos(0.5,1,1.5,2.5,5);
  private FachadaViandas fachadaViandas;
  private FachadaLogistica fachadaLogistica;

  public Fachada() {
  }

  @Override
  public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO) {
    Colaborador colaborador = new Colaborador(colaboradorDTO.getId(),colaboradorDTO.getNombre(),colaboradorDTO.getFormas());
    colaborador = this.colaboradorRepository.save(colaborador);
    return colaboradorMapper.map(colaborador);
  }

  @Override
  public ColaboradorDTO buscarXId(Long colaboradorId) throws NoSuchElementException {
    Colaborador colaborador = this.colaboradorRepository.findById(colaboradorId);
    return colaboradorMapper.map(colaborador);
  }


  @Override
  public Double puntos(Long colaboradorId) throws NoSuchElementException {
    List<ViandaDTO> viandasDTO = fachadaViandas.viandasDeColaborador(colaboradorId,1,2024);
    Integer viandasDonadas = viandasDTO.size();
    List<TrasladoDTO> trasladosDTO = fachadaLogistica.trasladosDeColaborador(colaboradorId,1,2024);
    Integer traslados = trasladosDTO.size();

    return
        coeficientesPuntos.getValor(TipoCoeficiente.VIANDAS_DONADAS) * viandasDonadas +
            coeficientesPuntos.getValor(TipoCoeficiente.VIANDAS_DISTRIBUIDAS) * traslados;
  }

  @Override
  public ColaboradorDTO modificar(Long colaboradorId, List<FormaDeColaborarEnum> formas) throws NoSuchElementException {
    Colaborador colaborador = this.colaboradorRepository.findById(colaboradorId);

    if (colaborador == null) {
      throw new NoSuchElementException("No se encontró ningún colaborador con el ID especificado: " + colaboradorId);
    }

    colaborador.setFormas(formas);

    this.colaboradorRepository.update(colaborador);

    return colaboradorMapper.map(colaborador);
  }
  @Override
  public void actualizarPesosPuntos(Double pesosDonados, Double viandasDistribuidas, Double viandasDonadas, Double tarjetasRepartidas, Double heladerasActivas) {
    coeficientesPuntos.setValor(TipoCoeficiente.PESOS_DONADOS, pesosDonados);
    coeficientesPuntos.setValor(TipoCoeficiente.VIANDAS_DISTRIBUIDAS, viandasDistribuidas);
    coeficientesPuntos.setValor(TipoCoeficiente.VIANDAS_DONADAS, viandasDonadas);
    coeficientesPuntos.setValor(TipoCoeficiente.TARJETAS_REPARTIDAS, tarjetasRepartidas);
    coeficientesPuntos.setValor(TipoCoeficiente.HELADERAS_ACTIVAS, heladerasActivas);
  }

  @Override
  public void setLogisticaProxy(FachadaLogistica fachadaLogistica) {
    this.fachadaLogistica = fachadaLogistica;
  }

  @Override
  public void setViandasProxy(FachadaViandas fachadaViandas) {
    this.fachadaViandas = fachadaViandas;
  }
}
