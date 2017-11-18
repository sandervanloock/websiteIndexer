import {BaseEntity} from './../../shared';

export class FeedItem implements BaseEntity {
    constructor( public id?: number, public text?: string ) {
    }
}
