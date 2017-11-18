import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {JhiDateUtils} from 'ng-jhipster';

import {Document} from './document.model';
import {createRequestOption, ResponseWrapper} from '../../shared';

@Injectable()
export class DocumentService {

    private resourceUrl = 'api/documents';

    constructor( private http: Http, private dateUtils: JhiDateUtils ) {
    }

    create( document: Document ): Observable<Document> {
        const copy = this.convert( document );
        return this.http.post( this.resourceUrl, copy ).map( ( res: Response ) => {
            const jsonResponse = res.json();
            this.convertItemFromServer( jsonResponse );
            return jsonResponse;
        } );
    }

    update( document: Document ): Observable<Document> {
        const copy = this.convert( document );
        return this.http.put( this.resourceUrl, copy ).map( ( res: Response ) => {
            const jsonResponse = res.json();
            this.convertItemFromServer( jsonResponse );
            return jsonResponse;
        } );
    }

    find( id: number ): Observable<Document> {
        return this.http.get( `${this.resourceUrl}/${id}` ).map( ( res: Response ) => {
            const jsonResponse = res.json();
            this.convertItemFromServer( jsonResponse );
            return jsonResponse;
        } );
    }

    query( req?: any ): Observable<ResponseWrapper> {
        const options = createRequestOption( req );
        return this.http.get( this.resourceUrl, options )
            .map( ( res: Response ) => this.convertResponse( res ) );
    }

    delete( id: number ): Observable<Response> {
        return this.http.delete( `${this.resourceUrl}/${id}` );
    }

    private convertResponse( res: Response ): ResponseWrapper {
        const jsonResponse = res.json();
        for ( let i = 0; i < jsonResponse.length; i++ ) {
            this.convertItemFromServer( jsonResponse[i] );
        }
        return new ResponseWrapper( res.headers, jsonResponse, res.status );
    }

    private convertItemFromServer( entity: any ) {
        entity.created = this.dateUtils
            .convertDateTimeFromServer( entity.created );
    }

    private convert( document: Document ): Document {
        const copy: Document = Object.assign( {}, document );

        copy.created = this.dateUtils.toDate( document.created );
        return copy;
    }
}
