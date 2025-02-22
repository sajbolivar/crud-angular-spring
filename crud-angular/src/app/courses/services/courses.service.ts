import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { first, of, tap } from 'rxjs';

import { Course } from '../model/course';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {
  private readonly API = '/api/courses';

  private cache: Course[] = [];

  constructor(private http: HttpClient) { }

  list() {
    return this.http.get<Course[]>(this.API).pipe(
      first(),
      tap(data => (this.cache = data))
    );
  }

  loadById(id: string) {
    if (this.cache.length > 0) {
      const record = this.cache.find(curso => `${curso._id}` === `${id}`);
      return record != null ? of(record) : this.getById(id);
    }
    return this.getById(id);
  }

  private getById(id: string) {
    return this.http.get<Course>(`${this.API}/${id}`).pipe(first());
  }

  save(record: Partial<Course>) {
    if (record._id) {
      return this.update(record);
    }
    return this.create(record);
  }

  private update(record: Partial<Course>) {
    return this.http.put<Course>(`${this.API}/${record._id}`, record).pipe(first());
  }

  private create(record: Partial<Course>) {
    return this.http.post<Course>(this.API, record).pipe(first());
  }

  remove(id: string) {
    return this.http.delete<Course>(`${this.API}/${id}`).pipe(first());
  }
}
