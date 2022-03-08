export interface IAvion {
  id?: number;
  tipo?: string;
  matricula?: string;
  edad?: number | null;
  numeroSerie?: string;
}

export class Avion implements IAvion {
  constructor(
    public id?: number,
    public tipo?: string,
    public matricula?: string,
    public edad?: number | null,
    public numeroSerie?: string
  ) {}
}

export function getAvionIdentifier(avion: IAvion): number | undefined {
  return avion.id;
}
