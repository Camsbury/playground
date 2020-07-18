module Vect
import Data.Vect
import Data.Fin
%default total

vectTake : (m : Fin (S n)) -> Vect n a -> Vect (finToNat m) a
vectTake FZ xs = []
vectTake (FS y) (x :: xs) = x :: vectTake y xs

sumEntriesFin : Num a => (pos : Fin n) -> Vect n a -> Vect n a -> a
sumEntriesFin pos xs ys = Vect.index pos xs + Vect.index pos ys

sumEntriesInt : Num a => (pos : Integer) -> Vect n a -> Vect n a -> Maybe a
sumEntriesInt {n} pos xs ys = case integerToFin pos n of
                                Nothing => Nothing
                                Just idx => Just $ index idx xs + index idx ys


class Counted where
getIndexForCounted : (Type -> Type) -> Type
getTaken : (cont : (Type -> Type)) -> (m : getIndexForCounted cont) -> Type -> Type

take
  : Counted c
  => (m : getIndexForCounted cont)
  -> cont a
  -> (getTakenCont cont m) a
