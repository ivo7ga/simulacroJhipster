import { IAeropuerto } from 'app/entities/aeropuerto/aeropuerto.model';
import { IAvion } from 'app/entities/avion/avion.model';
import { IPiloto } from 'app/entities/piloto/piloto.model';
import { ITripulante } from 'app/entities/tripulante/tripulante.model';

export interface IVuelo {
  id?: number;
  pasaporteCovid?: boolean;
  numeroDeVuelo?: string;
  aeropuerto?: IAeropuerto | null;
  destinoAeropuerto?: IAeropuerto | null;
  avion?: IAvion | null;
  piloto?: IPiloto | null;
  tripulantes?: ITripulante[] | null;
}

export class Vuelo implements IVuelo {
  constructor(
    public id?: number,
    public pasaporteCovid?: boolean,
    public numeroDeVuelo?: string,
    public aeropuerto?: IAeropuerto | null,
    public destinoAeropuerto?: IAeropuerto | null,
    public avion?: IAvion | null,
    public piloto?: IPiloto | null,
    public tripulantes?: ITripulante[] | null
  ) {
    this.pasaporteCovid = this.pasaporteCovid ?? false;
  }
}

export function getVueloIdentifier(vuelo: IVuelo): number | undefined {
  return vuelo.id;
}
