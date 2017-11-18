import {BaseEntity} from './../../shared';

export class Attribute implements BaseEntity {
    constructor( public id?: number, public value?: string, public document?: BaseEntity, public selector?: BaseEntity, public relatives?: BaseEntity[] ) {
    }
}
