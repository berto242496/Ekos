import { CharacterItem } from '../types';

interface CharacterAvatarProps {
  character: Pick<CharacterItem, 'name' | 'avatarImage'>;
  className?: string;
}

export function CharacterAvatar({ character, className = 'h-8 w-8' }: CharacterAvatarProps) {
  return (
    <img
      src={character.avatarImage}
      alt={character.name}
      className={`${className} shrink-0 rounded-full object-cover`}
      draggable={false}
    />
  );
}
