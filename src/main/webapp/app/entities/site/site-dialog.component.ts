import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Response} from '@angular/http';

import {Observable} from 'rxjs/Rx';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Site} from './site.model';
import {SitePopupService} from './site-popup.service';
import {SiteService} from './site.service';

@Component({
    selector: 'jhi-site-dialog',
    templateUrl: './site-dialog.component.html'
})
export class SiteDialogComponent implements OnInit {

    site: Site;
    isSaving: boolean;

    constructor(public activeModal: NgbActiveModal,
                private alertService: JhiAlertService,
                private siteService: SiteService,
                private eventManager: JhiEventManager) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.site.id !== undefined) {
            this.subscribeToSaveResponse(
                this.siteService.update(this.site));
        } else {
            this.subscribeToSaveResponse(
                this.siteService.create(this.site));
        }
    }

    private subscribeToSaveResponse(result: Observable<Site>) {
        result.subscribe((res: Site) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Site) {
        this.eventManager.broadcast({name: 'siteListModification', content: 'OK'});
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
    selector: 'jhi-site-popup',
    template: ''
})
export class SitePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(private route: ActivatedRoute,
                private sitePopupService: SitePopupService) {
    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['id']) {
                this.sitePopupService
                    .open(SiteDialogComponent as Component, params['id']);
            } else {
                this.sitePopupService
                    .open(SiteDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
