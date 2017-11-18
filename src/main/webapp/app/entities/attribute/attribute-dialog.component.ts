import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Response} from '@angular/http';

import {Observable} from 'rxjs/Rx';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Attribute} from './attribute.model';
import {AttributePopupService} from './attribute-popup.service';
import {AttributeService} from './attribute.service';
import {Document, DocumentService} from '../document';
import {Selector, SelectorService} from '../selector';
import {ResponseWrapper} from '../../shared';

@Component( {
                selector: 'jhi-attribute-dialog', templateUrl: './attribute-dialog.component.html'
            } )
export class AttributeDialogComponent implements OnInit {

    attribute: Attribute;
    isSaving: boolean;

    documents: Document[];

    selectors: Selector[];

    attributes: Attribute[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private attributeService: AttributeService,
        private documentService: DocumentService,
        private selectorService: SelectorService,
        private eventManager: JhiEventManager ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.documentService.query()
            .subscribe( ( res: ResponseWrapper ) => {
                this.documents = res.json;
            }, ( res: ResponseWrapper ) => this.onError( res.json ) );
        this.selectorService.query()
            .subscribe( ( res: ResponseWrapper ) => {
                this.selectors = res.json;
            }, ( res: ResponseWrapper ) => this.onError( res.json ) );
        this.attributeService.query()
            .subscribe( ( res: ResponseWrapper ) => {
                this.attributes = res.json;
            }, ( res: ResponseWrapper ) => this.onError( res.json ) );
    }

    clear() {
        this.activeModal.dismiss( 'cancel' );
    }

    save() {
        this.isSaving = true;
        if ( this.attribute.id !== undefined ) {
            this.subscribeToSaveResponse( this.attributeService.update( this.attribute ) );
        } else {
            this.subscribeToSaveResponse( this.attributeService.create( this.attribute ) );
        }
    }

    trackDocumentById( index: number, item: Document ) {
        return item.id;
    }

    trackSelectorById( index: number, item: Selector ) {
        return item.id;
    }

    trackAttributeById( index: number, item: Attribute ) {
        return item.id;
    }

    getSelected( selectedVals: Array<any>, option: any ) {
        if ( selectedVals ) {
            for ( let i = 0; i < selectedVals.length; i++ ) {
                if ( option.id === selectedVals[i].id ) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }

    private subscribeToSaveResponse( result: Observable<Attribute> ) {
        result.subscribe( ( res: Attribute ) => this.onSaveSuccess( res ), ( res: Response ) => this.onSaveError( res ) );
    }

    private onSaveSuccess( result: Attribute ) {
        this.eventManager.broadcast( {name: 'attributeListModification', content: 'OK'} );
        this.isSaving = false;
        this.activeModal.dismiss( result );
    }

    private onSaveError( error ) {
        try {
            error.json();
        } catch ( exception ) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError( error );
    }

    private onError( error ) {
        this.alertService.error( error.message, null, null );
    }
}

@Component( {
                selector: 'jhi-attribute-popup', template: ''
            } )
export class AttributePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor( private route: ActivatedRoute, private attributePopupService: AttributePopupService ) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe( ( params ) => {
            if ( params['id'] ) {
                this.attributePopupService
                    .open( AttributeDialogComponent as Component, params['id'] );
            } else {
                this.attributePopupService
                    .open( AttributeDialogComponent as Component );
            }
        } );
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
