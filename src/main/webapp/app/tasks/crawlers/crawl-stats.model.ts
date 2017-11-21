import {Site} from '../../entities/site/site.model';

export class CrawlStats {
    constructor( public Site: Site, public total?: number, public numberProcessed?: number, public status?: string, public startedOn?: any ) {
    }
}
