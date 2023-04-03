import {Triplet} from '../models/Triplet.model'

export interface Diet {
  id?: number;
  name: string;
  description: string;
  week: Triplet;
  weekInfo: string[7];
}