module Main
import Data.Vect
%default total


||| String storage
data DataStore : Type where
  MkData : (size : Nat) -> (items : Vect size String) -> DataStore

size : DataStore -> Nat
size (MkData size' items) = size'

items : (store : DataStore) -> Vect (size store) String
items (MkData size items') = items'

addToStore : DataStore -> String -> DataStore
addToStore (MkData size items) newItem = MkData _ (addToData items)
  where
    addToData : Vect old String -> Vect (S old) String
    addToData [] = [newItem]
    addToData (x :: xs) = x :: addToData xs



||| CLI commands for the `DataStore`
data Command = Add    String
             | Get    Integer
             | Search String
             | Size
             | Quit

parseCommand : (cmd : String) -> (args : String) -> Maybe Command
parseCommand "add" str = Just (Add str)
parseCommand "get" val = case all isDigit (unpack val) of
                              False => Nothing
                              True => Just (Get (cast val))
parseCommand "search" str = Just (Search str)
parseCommand "size" _ = Just Size
parseCommand "quit" _ = Just Quit
parseCommand _ _ = Nothing

parse : (input : String) -> Maybe Command
parse input = case span (/= ' ') input of
                   (cmd, args) => parseCommand cmd (ltrim args)

getEntry : (pos : Integer) -> (store : DataStore) -> Maybe (String, DataStore)
getEntry pos ds@(MkData size items) = case integerToFin pos size of
                                        Nothing => Just ("Out of range\n", ds)
                                        (Just idx) => Just $ (index idx items, ds)

||| Search the `DataStore` for occurrences of a given substring
searchStore
  : (str : String)
  -> (store : DataStore)
  -> Maybe (String, DataStore)
searchStore str (MkData size items)
  = case Vect.filter (Strings.isInfixOf str) items of
         case_val => ?searchStore_rhs_1

processInput : DataStore -> String -> Maybe (String, DataStore)
processInput store inp
  = case parse inp of
    Nothing => Just ("Invalid command\n", store)
    Just (Add item) => Just
      ( "ID " ++ show (size store) ++ "\n"
      , addToStore store item
      )
    Just (Get pos) => getEntry pos store
    Just (Search str) => searchStore str store
    Just Size => Just (show $ size store, store)
    Just Quit => Nothing

partial main : IO ()
main = replWith (MkData _ []) "Command: " processInput
