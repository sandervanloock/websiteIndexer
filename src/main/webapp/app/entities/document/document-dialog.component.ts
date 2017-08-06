import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Response} from '@angular/http';

import {Observable} from 'rxjs/Rx';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Document} from './document.model';
import {DocumentPopupService} from './document-popup.service';
import {DocumentService} from './document.service';
import {Site, SiteService} from '../site';
import {ResponseWrapper} from '../../shared';

@Component({
    selector: 'jhi-document-dialog',
    templateUrl: './document-dialog.component.html'
})
export class DocumentDialogComponent implements OnInit {

    document: Document;
    isSaving: boolean;

    documents: Document[];

    sites: Site[];

    constructor(public activeModal: NgbActiveModal,
                private alertService: JhiAlertService,
                private documentService: DocumentService,
                private siteService: SiteService,
                private eventManager: JhiEventManager) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.documentService.query()
            .subscribe((res: ResponseWrapper) => {
                this.documents = res.json;
            }, (res: ResponseWrapper) => this.onError(res.json));
        this.siteService.query()
            .subscribe((res: ResponseWrapper) => {
                this.sites = res.json;
            }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.document.id !== undefined) {
            this.subscribeToSaveResponse(
                this.documentService.update(this.document));
        } else {
            this.subscribeToSaveResponse(
                this.documentService.create(this.document));
        }
    }

    trackDocumentById(index: number, item: Document) {
        return item.id;
    }

    trackSiteById(index: number, item: Site) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }

    private subscribeToSaveResponse(result: Observable<Document>) {
        result.subscribe((res: Document) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Document) {
        this.eventManager.broadcast({name: 'documentListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-document-popup',
    template: ''
})
export class DocumentPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private route: ActivatedRoute,
                private documentPopupService: DocumentPopupService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['id']) {
                this.documentPopupService
                    .open(DocumentDialogComponent as Component, params['id']);
            } else {
                this.documentPopupService
                    .open(DocumentDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
