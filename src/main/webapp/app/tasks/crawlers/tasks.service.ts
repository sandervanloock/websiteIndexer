import {Injectable} from '@angular/core';
import {CrawlStats} from './crawl-stats.model';
import {Observable} from 'rxjs/Observable';
import {Http, Response} from '@angular/http';
import {Site} from '../../entities/site/site.model';

@Injectable()
export class TasksService {

    private resourceUrl = '/api/crawler';

    constructor( private http: Http ) {
    }

    stats(): Observable<CrawlStats[]> {
        return this.http.get( `${this.resourceUrl}` ).map( ( res: Response ) => {
            return res.json();
        } );
    }

    startCrawl( site: Site ): Observable<CrawlStats> {
        const copy: Site = this.convert( site );
        return this.http.post( `${this.resourceUrl}/${site.id}/start`, copy ).map( ( res: Response ) => {
            return res.json();
        } );
    }

    private convert( site: Site ): Site {
        const copy: Site = Object.assign( {}, site );
        return copy;
    }
}
