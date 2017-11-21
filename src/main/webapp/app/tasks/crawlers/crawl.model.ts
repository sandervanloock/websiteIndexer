import {Site} from '../../entities/site/site.model';

export class Crawl {
    constructor( public site?: Site, public seed?: string ) {
    }
}
