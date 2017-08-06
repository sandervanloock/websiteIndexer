import {BaseEntity} from './../../shared';

export class Document implements BaseEntity {
    constructor(public id?: number,
                public created?: any,
                public url?: string,
                public matches?: BaseEntity[],
                public site?: BaseEntity,
                public attributes?: BaseEntity[],) {
    }
}
