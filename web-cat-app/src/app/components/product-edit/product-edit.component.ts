import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ProductsServices} from "../../services/products.services";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-product-edit',
  templateUrl: './product-edit.component.html',
  styleUrls: ['./product-edit.component.css']
})
export class ProductEditComponent implements OnInit {
  productId!: number;
  productFormGroup!: FormGroup;

  constructor(private fb: FormBuilder, private activatedRoute: ActivatedRoute, private productsService: ProductsServices, private router: Router) {
    this.productId = activatedRoute.snapshot.params['id'];
  }

  ngOnInit(): void {
    this.productsService.getProduct(this.productId)
      .subscribe(product => {
        this.productFormGroup = this.fb.group(
          {
            id: [product.id, Validators.required],
            name: [product.name, Validators.required],
            price: [product.price, Validators.required],
            quantity: [product.quantity, Validators.required],
            selected: [product.selected, Validators.required],
            available: [product.available, Validators.required],
          }
        )
      });
  }

  onUpdateProduct() {
    this.productsService.updateProduct(this.productFormGroup.value)
      .subscribe(data => {
        alert("success Product Updated");
        this.router.navigateByUrl("/products");
      })
  }
}
