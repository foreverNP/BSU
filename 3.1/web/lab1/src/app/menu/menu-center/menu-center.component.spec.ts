import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuCenterComponent } from './menu-center.component';

describe('MenuCenterComponent', () => {
  let component: MenuCenterComponent;
  let fixture: ComponentFixture<MenuCenterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MenuCenterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuCenterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
