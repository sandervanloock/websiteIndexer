import {BaseEntity} from './../../shared';

export class Site implements BaseEntity {
    constructor( public id?: number,
                 public name?: string,
                 public regex?: string,
                 public seed?: string,
                 public pages?: BaseEntity[], public selectors?: BaseEntity[] )
    {
    }
}
