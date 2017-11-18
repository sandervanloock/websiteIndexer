import {BaseEntity} from './../../shared';

export class Selector implements BaseEntity {
    constructor(
        public id?: number,
        public value?: string,
        public name?: string,
        public attribute?: string,
        public isPrimary?: boolean,
        public site?: BaseEntity,
        public children?: BaseEntity[],
        public parent?: BaseEntity ) {
        this.isPrimary = false;
    }
}
