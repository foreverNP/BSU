import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MenuService } from '../services/menu.service';
import { MenuItem } from '../models/menu.model';

@Component({
  selector: 'app-menu-form',
  templateUrl: './menu-form.component.html',
  styleUrls: ['./menu-form.component.css'],
})
export class MenuFormComponent implements OnInit {
  menuItems: MenuItem[] = [];
  selectedMenuItem: MenuItem = { id: '', title: '', price: 0, description: '' };

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.menuService.getMenuItems().subscribe((menus) => {
      this.menuItems = menus;
    });
  }

  onSubmit(form: NgForm): void {
    if (form.invalid) {
      return;
    }

    if (this.selectedMenuItem.id) {
      this.menuService.updateMenuItem(this.selectedMenuItem);
    } else {
      this.menuService.addMenuItem({
        title: form.value.title,
        price: form.value.price,
        description: form.value.description,
      });
    }

    this.menuService.getMenuItems().subscribe((menus) => {
      this.menuItems = menus;
    });

    this.resetForm(form);
  }

  onEdit(menuItem: MenuItem): void {
    this.selectedMenuItem = { ...menuItem };
  }

  onDelete(id: string): void {
    this.menuService.deleteMenuItem(id);
    this.menuItems = this.menuItems.filter((menu) => menu.id !== id);
  }

  resetForm(form: NgForm): void {
    form.resetForm();
    this.selectedMenuItem = { id: '', title: '', price: 0, description: '' };
  }
}
