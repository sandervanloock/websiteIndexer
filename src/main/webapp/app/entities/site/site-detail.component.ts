import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {JhiEventManager} from 'ng-jhipster';

import {Site} from './site.model';
import {SiteService} from './site.service';

@Component({
    selector: 'jhi-site-detail',
    templateUrl: './site-detail.component.html'
})
export class SiteDetailComponent implements OnInit, OnDestroy {

    site: Site;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(private eventManager: JhiEventManager,
                private siteService: SiteService,
                private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSites();
    }

    load(id) {
        this.siteService.find(id).subscribe((site) => {
            this.site = site;
        });
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSites() {
        this.eventSubscriber = this.eventManager.subscribe(
            'siteListModification',
            (response) => this.load(this.site.id)
        );
    }
}
