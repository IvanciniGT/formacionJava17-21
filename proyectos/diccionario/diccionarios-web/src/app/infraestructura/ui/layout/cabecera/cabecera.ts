import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SelectorDeTema } from '../../../tema/selector-de-tema/selector-de-tema';

@Component({
  selector: 'app-cabecera',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, SelectorDeTema],
  templateUrl: './cabecera.html',
  styleUrl: './cabecera.css',
})
export class Cabecera {}
