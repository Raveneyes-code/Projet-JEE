import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProductsServices} from "../../services/products.services";
import {Router} from "@angular/router";

@Component({
  selector: 'app-product-add',
  templateUrl: './product-add.component.html',
  styleUrls: ['./product-add.component.css']
})
export class ProductAddComponent implements OnInit {

  submitted : boolean = false;
  productFormGroup!:FormGroup;

  constructor(private fb:FormBuilder,private productsService:ProductsServices,private router:Router) { }

  ngOnInit(): void {
    this.productFormGroup=this.fb.group({
      name:[null,Validators.required],
      price:[null,Validators.required],
      quantity:[null,Validators.required],
      selected:[true,Validators.required],
      available:[true,Validators.required],

    });
  }

  onSaveProduct() {
    this.submitted=true;
    if(this.productFormGroup.invalid)return;
    this.productsService.saveProduct(this.productFormGroup.value)
      .subscribe(data=>{
        alert("Success Saving product");
        this.router.navigateByUrl("/products");
      });
  }

  get name(){
    return this.productFormGroup.get('name');
  }
}
