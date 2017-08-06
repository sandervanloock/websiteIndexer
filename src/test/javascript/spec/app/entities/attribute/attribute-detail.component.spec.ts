/* tslint:disable max-line-length */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {OnInit} from '@angular/core';
import {DatePipe} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {JhiDataUtils, JhiDateUtils, JhiEventManager} from 'ng-jhipster';
import {WebsiteIndexerTestModule} from '../../../test.module';
import {MockActivatedRoute} from '../../../helpers/mock-route.service';
import {AttributeDetailComponent} from '../../../../../../main/webapp/app/entities/attribute/attribute-detail.component';
import {AttributeService} from '../../../../../../main/webapp/app/entities/attribute/attribute.service';
import {Attribute} from '../../../../../../main/webapp/app/entities/attribute/attribute.model';

describe('Component Tests', () => {

    describe('Attribute Management Detail Component', () => {
        let comp: AttributeDetailComponent;
        let fixture: ComponentFixture<AttributeDetailComponent>;
        let service: AttributeService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [WebsiteIndexerTestModule],
                declarations: [AttributeDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    AttributeService,
                    JhiEventManager
                ]
            }).overrideTemplate(AttributeDetailComponent, '')
                .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(AttributeDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AttributeService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new Attribute(10)));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.attribute).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
