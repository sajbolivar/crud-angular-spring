import { HttpErrorResponse } from '@angular/common/http';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { CoursesService } from './courses.service';
import { coursesMock } from './couses.mock';

describe('CoursesService', () => {
  let service: CoursesService;
  let httpTestingController: HttpTestingController;
  const API = '/api/courses';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CoursesService]
    });
    service = TestBed.inject(CoursesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should list all courses', () => {
    service.list().subscribe(courses => {
      expect(courses).toBeTruthy();
      expect(courses.length).toBe(coursesMock.length);
    });

    const req = httpTestingController.expectOne(API);
    expect(req.request.method).toEqual('GET');
    req.flush(coursesMock);
  });

  it('should list course by id', () => {
    service.loadById('1').subscribe(course => {
      expect(course).toBeTruthy();
      expect(course._id).toBe('1');
    });

    const req = httpTestingController.expectOne(`${API}/1`);
    expect(req.request.method).toEqual('GET');
    req.flush(coursesMock[0]);
  });

  it('should list course by id from cache', () => {
    // hack to set private properties
    service['cache'] = coursesMock;

    service.loadById('1').subscribe(course => {
      expect(course).toBeTruthy();
      expect(course._id).toBe('1');
    });

    httpTestingController.expectNone(`${API}/1`);
  });

  it('should list course by id by checking cache', () => {
    // hack to set private properties
    service['cache'] = [coursesMock[0]];

    service.loadById('2').subscribe(course => {
      expect(course).toBeTruthy();
      expect(course._id).toBe('2');
    });

    const req = httpTestingController.expectOne(`${API}/2`);
    expect(req.request.method).toEqual('GET');
    req.flush(coursesMock[1]);
  });

  it('should save a new course', () => {
    const curso = { _id: undefined, name: 'Testes no Angular', category: 'front-end' };

    service.save(curso).subscribe(course => {
      expect(course).toBeTruthy();
      expect(course._id).toBeTruthy();
      expect(course.name).toEqual(curso.name);
      expect(course.category).toEqual(curso.category);
    });

    const req = httpTestingController.expectOne(API);
    expect(req.request.body['name']).toEqual(curso.name);
    expect(req.request.method).toEqual('POST');
    req.flush({ ...curso, _id: 123 });
  });

  it('should update an existing course', () => {
    const curso = { _id: '1', name: 'Testes no Angular', category: 'front-end' };

    service.save(curso).subscribe(course => {
      expect(course).toBeTruthy();
      expect(course._id).toEqual(curso._id);
      expect(course.name).toEqual(curso.name);
      expect(course.category).toEqual(curso.category);
    });

    const req = httpTestingController.expectOne(`${API}/1`);
    expect(req.request.body['name']).toEqual(curso.name);
    expect(req.request.method).toEqual('PUT');
    req.flush({ ...curso });
  });

  it('should give an error if save course fails', () => {
    const curso = { _id: '111', name: 'Testes no Angular', category: 'front-end' };

    service.save(curso).subscribe(
      () => {
        fail('the save course operation should have failed');
      },
      (error: HttpErrorResponse) => {
        expect(error.status).toBe(500);
      }
    );

    const req = httpTestingController.expectOne(`${API}/111`);
    expect(req.request.method).toEqual('PUT');
    req.flush('Save course failed', { status: 500, statusText: 'Internal Server Error' });
  });

  it('should give an error if course does not exist', () => {
    service.loadById('111').subscribe(
      () => {
        fail('course should not exist');
      },
      (error: HttpErrorResponse) => {
        expect(error.status).toBe(404);
      }
    );

    const req = httpTestingController.expectOne(`${API}/111`);
    expect(req.request.method).toEqual('GET');
    req.flush('Course not found', { status: 404, statusText: 'Not found' });
  });

  it('should remove course by id', () => {
    service.remove('1').subscribe(result => {
      expect(result).toBeFalsy();
    });

    const req = httpTestingController.expectOne(`${API}/1`);
    expect(req.request.method).toEqual('DELETE');
    req.flush(null);
  });

  afterEach(() => {
    httpTestingController.verify();
  });
});
