import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MenuCenterComponent } from './menu-center/menu-center.component';
import { MenuListComponent } from './menu-list/menu-list.component';
import { MenuDetailsComponent } from './menu-details/menu-details.component';
import { MenuFormComponent } from './menu-form/menu-form.component';

const routes: Routes = [
  {
    path: '',
    component: MenuCenterComponent,
    children: [
      { path: '', component: MenuListComponent },
      { path: 'details/:id', component: MenuDetailsComponent },
      { path: 'form', component: MenuFormComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MenuRoutingModule {}
