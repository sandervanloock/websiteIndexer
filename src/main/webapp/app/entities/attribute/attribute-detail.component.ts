import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {JhiEventManager} from 'ng-jhipster';

import {Attribute} from './attribute.model';
import {AttributeService} from './attribute.service';

@Component({
    selector: 'jhi-attribute-detail',
    templateUrl: './attribute-detail.component.html'
})
export class AttributeDetailComponent implements OnInit, OnDestroy {

    attribute: Attribute;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(private eventManager: JhiEventManager,
                private attributeService: AttributeService,
                private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInAttributes();
    }

    load(id) {
        this.attributeService.find(id).subscribe((attribute) => {
            this.attribute = attribute;
        });
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInAttributes() {
        this.eventSubscriber = this.eventManager.subscribe(
            'attributeListModification',
            (response) => this.load(this.attribute.id)
        );
    }
}
