import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MenuCenterComponent } from './menu-center/menu-center.component';
import { MenuListComponent } from './menu-list/menu-list.component';
import { MenuDetailsComponent } from './menu-details/menu-details.component';
import { FormsModule } from '@angular/forms';

import { MenuRoutingModule } from './menu-routing.module';
import { MenuFormComponent } from './menu-form/menu-form.component';

@NgModule({
  declarations: [
    MenuCenterComponent,
    MenuListComponent,
    MenuDetailsComponent,
    MenuFormComponent,
  ],
  imports: [CommonModule, FormsModule, MenuRoutingModule],
})
export class MenuModule {}
