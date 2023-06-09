import { HttpClient } from "@angular/common/http";
import { Form } from "../models/Formulary.model";
import { Injectable } from "@angular/core";

const BASE_URL = '/api/forms';

@Injectable({ providedIn: 'root' })
export class FormService{
    Form:Form|undefined;

    constructor(private httpClient: HttpClient){

    }

    createForm(form: Form){
        return this.httpClient.post(BASE_URL+'/',form);
    }
}

