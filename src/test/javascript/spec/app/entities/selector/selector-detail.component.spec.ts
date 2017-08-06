/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {OnInit} from '@angular/core';
import {DatePipe} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {JhiDataUtils, JhiDateUtils, JhiEventManager} from 'ng-jhipster';
import {WebsiteIndexerTestModule} from '../../../test.module';
import {MockActivatedRoute} from '../../../helpers/mock-route.service';
import {SelectorDetailComponent} from '../../../../../../main/webapp/app/entities/selector/selector-detail.component';
import {SelectorService} from '../../../../../../main/webapp/app/entities/selector/selector.service';
import {Selector} from '../../../../../../main/webapp/app/entities/selector/selector.model';

describe('Component Tests', () => {

    describe('Selector Management Detail Component', () => {
        let comp: SelectorDetailComponent;
        let fixture: ComponentFixture<SelectorDetailComponent>;
        let service: SelectorService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [WebsiteIndexerTestModule],
                declarations: [SelectorDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    SelectorService,
                    JhiEventManager
                ]
            }).overrideTemplate(SelectorDetailComponent, '')
                .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SelectorDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SelectorService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new Selector(10)));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.selector).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
