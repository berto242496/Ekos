import { GuideAuthor } from '../types';

export const MOCK_GUIDES: GuideAuthor[] = [
  {
    id: 'ana-rodriguez',
    name: 'Ana Rodríguez',
    title: 'Guía oficial en Tui',
    bio: 'Historiadora y guía oficial de Galicia acreditada (nº GO-842). Especializada en la frontera histórica del Bajo Miño, la catedral-fortaleza de Tui y las viejas rutas del contrabando.',
    location: 'Tui / Valença do Minho',
    avatarUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=400&q=80',
    rating: 4.9,
    reviewsCount: 142,
    verified: true,
    routesCount: 4,
  },
  {
    id: 'marcos-varela',
    name: 'Marcos Varela',
    title: 'Experto en patrimonio local',
    bio: 'Arqueólogo y divulgador etnográfico. Dedicado al estudio de los petroglifos de los castros, fortalezas de frontera y leyendas fluviales del río Miño y de la Ribeira Sacra.',
    location: 'Frontera del Miño · Ourense / Pontevedra',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80',
    rating: 4.8,
    reviewsCount: 98,
    verified: true,
    routesCount: 3,
  },
  {
    id: 'lucia-fernandez',
    name: 'Lucía Fernández',
    title: 'Guía cultural en Galicia',
    bio: 'Especialista en tradición oral marinera, mitología costera de la Costa da Morte y villas históricas atlánticas. Licenciada en Humanidades por la Universidad de Santiago.',
    location: 'Costa Atlántica · Rías Baixas y Altas',
    avatarUrl: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=400&q=80',
    rating: 5.0,
    reviewsCount: 87,
    verified: true,
    routesCount: 3,
  },
];

export const DEFAULT_ACTIVE_GUIDE = MOCK_GUIDES[0]; // Ana Rodríguez by default
